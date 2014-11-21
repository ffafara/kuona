package kuona.config;

import kuona.server.JenkinsServer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicationConfigurationReaderTests {

    @Test
    public void readsProjectName() throws IOException {
        ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();
        KuonaSpec config = configurationReader.read(IOUtils.toInputStream("site:\n" +
                "  name: foo\n" +
                "  path: _site\n"));

        assertThat(config.getSiteName(), is("foo"));
        assertThat(config.getSitePath(), is("_site"));
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
        KuonaSpec config = configurationReader.read(IOUtils.toInputStream(configText));

        final List<JenkinsServer> servers = config.buildProcessors();

        assertThat(servers.size(), is(1));
    }

    @Test

    public void readsRepositories() {
        String configText = "repositories:\n" +
                "  - url: https://example.com\n" +
                "    processor: kuona.Subversion";

        ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();
        final KuonaSpec config = configurationReader.read(IOUtils.toInputStream(configText));

        final List<RepositorySpec> repos = config.getRepositories();

        assertThat(repos.size(), is(1));
        assertThat(repos.get(0).getUrl(), is("https://example.com"));
        assertThat(repos.get(0).getProcessor(), is("kuona.Subversion"));
    }
}
