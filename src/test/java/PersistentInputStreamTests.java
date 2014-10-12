import kuona.utils.PersistentInputStream;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersistentInputStreamTests {
    @Test
    public void returnsDataFromChildStream() throws IOException {
        InputStream base = new ByteArrayInputStream("a".getBytes());

        InputStream testStream = new PersistentInputStream(base);

        assertThat(testStream.read(), is((int) 'a'));
        assertThat(testStream.read(), is(-1));
    }

    @Test
    public void streamWritesToSuppliedOutputStream() throws IOException {
        InputStream base = new ByteArrayInputStream("a".getBytes());

        OutputStream output = new ByteArrayOutputStream();
        InputStream testStream = new PersistentInputStream(output, base);

        assertThat(testStream.read(), is((int) 'a'));
        assertThat(testStream.read(), is(-1));

        assertThat(output.toString(), is("a"));
    }
}
