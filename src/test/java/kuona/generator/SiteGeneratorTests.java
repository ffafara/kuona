package kuona.generator;

import org.junit.Test;

import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SiteGeneratorTests {

    @Test
    public void createsFoldersForResourcesWithoutSeparators() {
        Site site = mock(Site.class);

        SiteGenerator generator = new SiteGenerator(site, SiteGenerator.Options.NO_CONFIG);

        generator.generate("test-site","test-foo");

        verify(site).createDirectory("test-foo/_site");
        verify(site).createDirectory("test-foo/_site/test-folder");
        verify(site).createFile(eq("test-foo/_site/test-folder/index.html"), any(InputStream.class));
    }

    @Test
    public void createsFilesForResourcesWithSeparators() {
        Site site = mock(Site.class);

        SiteGenerator generator = new SiteGenerator(site, SiteGenerator.Options.NO_CONFIG);

        generator.generate("test-site/test-folder", "test-foo");

        verify(site).createFile(eq("test-foo/_site/index.html"), any(InputStream.class));
    }
}
