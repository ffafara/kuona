package kuona;

import kuona.model.Project;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProjectTests {
    @Test
    public void projectsAreNamedAfterDomain() throws MalformedURLException, URISyntaxException {
        final Project project = new Project(new URI("https://a:b@sub.example.com:8080"));

        assertThat(project.getName(), is("sub.example.com"));
    }

    @Test
    public void openCreatesFileForWritingBasedOnName() throws IOException, URISyntaxException {
        final Project project = new Project(new URI("https://a:b@sub.example.com:8080"));

        OutputStream output = project.openOutputSteam("/", "foo.txt");
        output.write("Hello".getBytes());
        output.close();

        final File testFile = new File("sub.example.com/foo.txt");
        assertThat(testFile.exists(), is(true));

        if (testFile.exists())
            testFile.delete();

        FileUtils.deleteDirectory(new File("sub.example.com"));
    }
}
