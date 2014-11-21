package kuona.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ServerSpecTests {
    @Test
    public void deserializableFromYAML() throws IOException {
        String jsonText = "name: My Server\n" +
                "url: some/path\n" +
                "processor: kuona.Jenkins\n" +
                "username: foo\n" +
                "password: bar\n";

        YAMLFactory factory = new YAMLFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        BuildServerSpec value = mapper.readValue(jsonText, BuildServerSpec.class);

        assertThat(value.getUrl(), is("some/path"));
        assertThat(value.getProcessor(), is("kuona.Jenkins"));
        assertThat(value.getName(), is("My Server"));
        assertThat(value.getUsername(), is("foo"));
        assertThat(value.getPassword(), is("bar"));
    }

}
