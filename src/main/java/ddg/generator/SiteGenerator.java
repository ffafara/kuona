package ddg.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SiteGenerator {
    private final Site site;


    public SiteGenerator(Site site) {
        this.site = site;
    }

    public void generate(String resourceRoot, String name) {
        ClassLoader loader = SiteGenerator.class.getClassLoader();

        InputStream rootStream = loader.getResourceAsStream(resourceRoot);

        BufferedReader reader = new BufferedReader(new InputStreamReader(rootStream));

        site.createDirectory(name);

        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("."))
                    site.createFile(name + File.separator + line, loader.getResourceAsStream(resourceRoot + File.separatorChar + line));
                else {
                    generate(resourceRoot + File.separator + line, name + File.separatorChar + line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
