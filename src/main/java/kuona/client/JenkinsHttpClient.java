/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.client;

import kuona.client.validator.HttpResponseValidator;
import kuona.model.BaseModel;
import kuona.model.Project;
import kuona.utils.Deserializer;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class JenkinsHttpClient implements JenkinsClient {

    private URI uri;
    private DefaultHttpClient client;
    private BasicHttpContext localContext;
    private HttpResponseValidator httpResponseValidator;

    private String context;
    private Project project;

    /**
     * Create an unauthenticated Jenkins HTTP client
     *
     * @param uri               Location of the jenkins server (ex. http://localhost:8080)
     * @param defaultHttpClient Configured DefaultHttpClient to be used
     */
    public JenkinsHttpClient(Project project, URI uri, DefaultHttpClient defaultHttpClient) {
        this.context = uri.getPath();
        if (!context.endsWith("/")) {
            context += "/";
        }
        this.uri = uri;
        this.project = project;
        this.client = defaultHttpClient;
        this.httpResponseValidator = new HttpResponseValidator();
    }

    /**
     * Create an unauthenticated Jenkins HTTP client
     *
     * @param uri Location of the jenkins server (ex. http://localhost:8080)
     */
    public JenkinsHttpClient(Project project, URI uri) {
        this(project, uri, new DefaultHttpClient());
    }

    /**
     * Create an authenticated Jenkins HTTP client
     *
     * @param uri      Location of the jenkins server (ex. http://localhost:8080)
     * @param username Username to use when connecting
     * @param password Password or auth token to use when connecting
     */
    public JenkinsHttpClient(Project project, URI uri, String username, String password) {
        this(project, uri);
        if (isNotBlank(username)) {
            CredentialsProvider provider = client.getCredentialsProvider();
            AuthScope scope = new AuthScope(uri.getHost(), uri.getPort(), "realm");
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(scope, credentials);

            localContext = new BasicHttpContext();
            localContext.setAttribute("preemptive-auth", new BasicScheme());
            client.addRequestInterceptor(new PreemptiveAuth(), 0);
        }
    }

    /**
     * Perform a GET request and parse the response to the given class
     *
     * @param path path to request, can be relative or absolute
     * @param cls  class of the response
     * @param <T>  type of the response
     * @return an instance of the supplied class
     * @throws IOException, HttpResponseException
     */
    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException {
        final Deserializer deserializer = project.getDeserializerFor(path);
        HttpGet getMethod = new HttpGet(api(path));
        HttpResponse response = client.execute(getMethod, localContext);
        try {
            httpResponseValidator.validateResponse(response);
            InputStream content = response.getEntity().getContent();
            T result = deserializer.objectFromResponse(cls, content);
            result.setClient(this);


            return result;
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(getMethod);
        }
    }

    /**
     * Perform a GET request and parse the response and return a simple string of the content
     *
     * @param path path to request, can be relative or absolute
     * @return the entity text
     * @throws IOException, HttpResponseException
     */
    public String get(String path, String contentType) throws IOException {
        HttpGet getMethod = new HttpGet(api(path));
        HttpResponse response = client.execute(getMethod, localContext);
        try {
            httpResponseValidator.validateResponse(response);
            Scanner s = new Scanner(response.getEntity().getContent());
            s.useDelimiter("\\z");
            StringBuffer sb = new StringBuffer();
            while (s.hasNext()) {
                sb.append(s.next());
            }
            return sb.toString();
        } finally {
            releaseConnection(getMethod);
        }
    }

    /**
     * Perform a GET request and return the response as InputStream
     *
     * @param path path to request, can be relative or absolute
     * @return the response stream
     * @throws IOException, HttpResponseException
     */
    public InputStream getFile(URI path) throws IOException {
        HttpGet getMethod = new HttpGet(path);
        try {
            HttpResponse response = client.execute(getMethod, localContext);
            httpResponseValidator.validateResponse(response);
            return response.getEntity().getContent();
        } finally {
            releaseConnection(getMethod);
        }
    }


    private String urlJoin(String path1, String path2) {
        if (!path1.endsWith("/")) {
            path1 += "/";
        }
        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }
        return path1 + path2;
    }

    private URI api(String path) {
        if (!path.toLowerCase().matches("https?://.*")) {
            path = urlJoin(this.context, path);
        }
        if (!path.contains("?")) {
            path = urlJoin(path, "api/json");
        } else {
            String[] components = path.split("\\?", 2);
            path = urlJoin(components[0], "api/json") + "?" + components[1];
        }
        return uri.resolve("/").resolve(path);
    }

    private void releaseConnection(HttpRequestBase httpRequestBase) {
        httpRequestBase.releaseConnection();
    }

    public URI getURI() {
        return uri;
    }
}
