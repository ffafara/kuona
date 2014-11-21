package kuona.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RepositorySpecTests {
    @Test

    public void deserializableFromJson() throws IOException {
        String jsonText = "url: https://some/path/\n" +
                "processor: kuona.Subversion\n" +
                "roots:\n" +
                "  - a\n" +
                "  - b\n" +
                "  - c\n";

        YAMLFactory factory = new YAMLFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        RepositorySpec value = mapper.readValue(jsonText, RepositorySpec.class);

        assertThat(value.getUrl(), is("https://some/path/"));
        assertThat(value.getRoots().size(), is(3));
    }

}
