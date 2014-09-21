package kuona.generator;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SiteGenerator {
    private final Site site;


    public SiteGenerator(Site site) {
        this.site = site;
    }

    public void generate(String resourceRoot, String name) {
        ClassLoader loader = getClass().getClassLoader();
        try {
            generateFromClasspath(resourceRoot, name, loader);
        } catch (Exception e) {
            generateFromJar(resourceRoot, name);
        }
        System.out.println("Site generated to " + name);
    }

    private void generateFromJar(String resourceRoot, String name) {
        try {
            CodeSource src = SiteGenerator.class.getProtectionDomain().getCodeSource();

            if (src != null) {

                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());

                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String filename = e.getName();

                    if (filename.startsWith(resourceRoot) && filename.length() > resourceRoot.length() + 1) {
                        String targetPath = filename.replace(resourceRoot + File.separatorChar, "");
                        if (targetPath.contains(".")) {
                            site.createFile(name + File.separatorChar + targetPath, zip);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void generateFromClasspath(String resourceRoot, String name, ClassLoader loader) {
        try {
            InputStream rootStream = loader.getResourceAsStream(resourceRoot);

            BufferedReader reader = new BufferedReader(new InputStreamReader(rootStream));

            site.createDirectory(name);

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
            throw new RuntimeException(e);
        }
    }
}
