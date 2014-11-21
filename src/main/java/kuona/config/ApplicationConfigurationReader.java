package kuona.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApplicationConfigurationReader {

    public static final String FILENAME = "config.yml";

    public boolean exists() {
        return new File(FILENAME).isFile();
    }

    public KuonaSpec read() {
        try {
            FileInputStream source = new FileInputStream(FILENAME);
            KuonaSpec config = read(source);
            source.close();
            return config;
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
    }

    public KuonaSpec read(InputStream source) {
        YAMLFactory factory = new YAMLFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.enableDefaultTyping();

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.registerSubtypes(BuildServerSpec.class);
        mapper.registerSubtypes(RepositorySpec.class);
        mapper.registerSubtypes(new NamedType(SiteSpec.class, "site"));

        try {
            return mapper.readValue(source, KuonaSpec.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed reading configuration", e);
        }
    }
}
