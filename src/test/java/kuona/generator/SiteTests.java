package kuona.generator;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SiteTests {

    @Test
    public void createsDirectory() {
        Site s = new Site();

        s.createDirectory("test.directory");

        assertTrue(new File("test.directory").isDirectory());
        new File("test.directory").delete();
    }

    @Test
    public void createsFileWithContents() throws IOException {
        Site s = new Site();

        String testFilename = "test.file";
        s.createFile(testFilename, new ByteArrayInputStream("Testing".getBytes()));

        assertTrue(new File(testFilename).isFile());

        byte[] encoded = Files.readAllBytes(Paths.get(testFilename));
        assertThat(new String(encoded), is(equalTo("Testing")));

        new File(testFilename).delete();
    }
}
