package kuona.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import kuona.processor.BuildProcessor;
import kuona.processor.JenkinsProcessor;
import kuona.processor.RepositoryProcessor;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class KuonaSpecTests {
    @Test
    public void deserializableFromYaml() throws IOException {
        String yaml = "site:\n" +
                "  path: test-site\n" +
                "servers:\n" +
                "  - name: my server\n" +
                "    url: http://some/path\n" +
                "    username: me\n" +
                "    password: pwd\n" +
                "repositories:\n" +
                "  - url: some/path\n" +
                "    username: foo\n" +
                "    password: bar\n" +
                "    roots:\n" +
                "      - a\n" +
                "      - b";

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

        KuonaSpec value = mapper.readValue(yaml, KuonaSpec.class);


        assertThat(value.getSitePath(), is("test-site"));
    }

    @Test
    public void deserializesSite() throws IOException {
        String yaml = "site:\n" +
                "  path: test-site\n";

        YAMLFactory factory = new YAMLFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.enableDefaultTyping();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.registerSubtypes(new NamedType(SiteSpec.class, "site"));

        KuonaSpec value = mapper.readValue(yaml, KuonaSpec.class);


        assertThat(value.getSitePath(), is("test-site"));
    }

    @Test
    public void providesJenkinsServers() {
        KuonaSpec spec = new KuonaSpec();
        spec.getBuildServers().add(new BuildServerSpec("foo", "/some/foo", "Jenkins", "", ""));

        final List<BuildProcessor> buildProcessors = spec.buildProcessors();
        assertThat(buildProcessors.size(), is(1));
        JenkinsProcessor j = (JenkinsProcessor) buildProcessors.get(0);
        assertThat(j.getURI().toString(), is("/some/foo"));
    }

    @Test
    public void providesRepositoryProcessors() {
        KuonaSpec spec = new KuonaSpec();
        spec.getRepositories().add(new RepositorySpec("", ""));

        List<RepositoryProcessor> processors = spec.repositoryProcessors();

        assertThat(processors.size(), is(1));
    }

    @Test
    public void canConfigureSubversionProcessors() {
        KuonaSpec spec = new KuonaSpec();
        spec.getRepositories().add(new RepositorySpec("some/path", "Subversion"));

        List<RepositoryProcessor> processors = spec.repositoryProcessors();

        assertThat(processors.size(), is(1));
    }
}
