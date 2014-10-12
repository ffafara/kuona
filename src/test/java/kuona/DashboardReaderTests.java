package kuona;

import kuona.model.DashboardModel;
import kuona.model.ServerEntry;
import kuona.utils.DashboardReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class DashboardReaderTests {
    @Test
    public void readsAverageBuildTime() {
        DashboardReader reader = new DashboardReader();

        DashboardModel model = reader.read(
                new ByteArrayInputStream("{\"averageBuildTime\": 123.0 }".getBytes()));

        assertThat(model.getAverageBuildTime(), is(123.0));
    }

    @Test
    public void readsVersionNumber() {
        DashboardReader reader = new DashboardReader();

        DashboardModel model = reader.read(
                new ByteArrayInputStream("{\"version\": \"1.2.4\" }".getBytes()));

        assertThat(model.getVersion(), is("1.2.4"));
    }

    @Test
    public void readsServerList() {
        String text = "{\"servers\":[{\"name\":null,\"description\":\"Welcome to the umisiri build server\",\"uri\":\"http://build.codekata.ninja\"}]}";

        DashboardReader reader = new DashboardReader();

        DashboardModel model = reader.read(new ByteArrayInputStream(text.getBytes()));

        assertThat(model.getServers().size(), is(1));
        ServerEntry first = model.getServers().get(0);
        assertThat(first.getName(), is(nullValue()));
        assertThat(first.getDescription(), is("Welcome to the umisiri build server"));
        assertThat(first.getUri(), is("http://build.codekata.ninja"));

    }
}
