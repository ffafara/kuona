package kuona.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import kuona.client.JenkinsHttpClient;
import kuona.client.JenkinsLocalClient;
import kuona.model.Project;
import kuona.server.JenkinsServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConfigurationReader {

    public static final String FILENAME = "config.yml";

    public boolean exists() {
        return new File(FILENAME).isFile();
    }

    public ApplicationConfiguration read() {
        try {
            FileInputStream source = new FileInputStream(FILENAME);
            ApplicationConfiguration config = read(source);
            source.close();
            return config;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationConfiguration read(InputStream source) {
        try {
            YAMLFactory factory = new YAMLFactory();
            ObjectMapper mapper = new ObjectMapper(factory);
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            HashMap<String, Object> o = mapper.readValue(source, typeRef);

            return new ApplicationConfiguration() {
                private final HashMap<String, Object> data = o;

                public String name() {
                    return data.get("name").toString();
                }

                public List<JenkinsServer> servers() {
                    final ArrayList<JenkinsServer> result = new ArrayList<>();
                    final ArrayList<Map<String, String>> servers = (ArrayList<Map<String, String>>) data.get("servers");

                    for (Map<String, String> m : servers) {
                        try {
                            String url = m.get("url");
                            String username = m.get("username");
                            String password = m.get("password");

                            final URI uri = new URI(url);
                            final Project project = new Project(uri);
                            final JenkinsLocalClient client = new JenkinsLocalClient(project, new JenkinsHttpClient(project, uri, username, password));

                            result.add(new JenkinsServer(client));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return result;
                }

                public String getSitePath() {
                    return (String) data.get("site-path");
                }

            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
