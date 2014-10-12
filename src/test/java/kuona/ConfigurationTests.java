package kuona;

import kuona.config.ApplicationConfiguration;
import kuona.config.ApplicationConfigurationReader;
import kuona.server.JenkinsServer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTests {
    @Test
    public void readsProjectName() throws IOException {
        ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();
        ApplicationConfiguration config = configurationReader.read(IOUtils.toInputStream("name: foo", "UTF-8"));

        assertThat(config.name(), is("foo"));
    }

    @Test
    public void readsServers() {
        String configText =
                "servers:\n" +
                        "  - name: Some name\n" +
                        "    url: http://example.com\n" +
                        "    username: test-username\n" +
                        "    password: test-password";

        ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();
        ApplicationConfiguration config = configurationReader.read(IOUtils.toInputStream(configText));

        final List<JenkinsServer> servers = config.servers();

        assertThat(servers.size(), is(1));
    }
}
