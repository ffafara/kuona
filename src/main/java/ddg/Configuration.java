package ddg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.offbytwo.jenkins.JenkinsServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configuration {
    private final HashMap<String, Object> data;

    public Configuration(HashMap<String, Object> data) {

        this.data = data;
    }

    public static Configuration read(InputStream source) {
        try {
            YAMLFactory factory = new YAMLFactory();
            ObjectMapper mapper = new ObjectMapper(factory);
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            HashMap<String, Object> o = mapper.readValue(source, typeRef);

            return new Configuration(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String name() {
        return data.get("name").toString();
    }

    public List<JenkinsServer> servers() {
//        JenkinsServer jenkins = new JenkinsServer(new URI(args[0]), args[1], args[2]);
//
//
        return new ArrayList<>();
    }
}
