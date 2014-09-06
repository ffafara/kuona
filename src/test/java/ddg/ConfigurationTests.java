package ddg;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTests {
    @Test
    public void readsProjectName() throws IOException {
        Configuration config = Configuration.read(IOUtils.toInputStream("name: foo", "UTF-8"));

        assertThat(config.name(), is("foo"));
    }
}
