package kuona.gocd.analyser;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class GoCdConfig {
    private String url, user, password;
    private URI uri;

    public GoCdConfig(String url, String user, String password) {
        if (url == null || url.isEmpty() || user == null || password == null) {
            throw new IllegalArgumentException("No data for GoCd Configuration");
        }
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL is not parsable");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public HttpHost getHost() {
        return URIUtils.extractHost(uri);
    }
}
