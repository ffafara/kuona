package kuona.generator;

import kuona.FileTemplate;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static kuona.FileTemplate.property;

public class SiteGenerator {
    private final Site site;
    private final Options options;

    public SiteGenerator(Site site) {
        this(site, Options.DEFAULT);
    }


    public SiteGenerator(Site site, Options options) {
        this.site = site;
        this.options = options;
    }

    public void generate(String resourceRoot, String name) {
        generateSite(resourceRoot, name + File.separatorChar + "_site");

        if (options != Options.NO_CONFIG) {
            generateConfigFile(name);
        }

        documentation(name);
    }

    private void generateSite(String resourceRoot, String sitePath) {
        ClassLoader loader = getClass().getClassLoader();
        try {
            generateFromClasspath(resourceRoot, sitePath, loader);
        } catch (Exception e) {
            generateFromJar(resourceRoot, sitePath);
        }
    }

    private void generateConfigFile(String name) {
        FileTemplate.get("config.yml")
                .with(property("name", name))
                .renderTo(name + File.separatorChar + "config.yml");
    }

    private void documentation(String name) {
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

    private void generateFromClasspath(String resourceRoot, String sitePath, ClassLoader loader) {
        try {
            InputStream rootStream = loader.getResourceAsStream(resourceRoot);

            BufferedReader reader = new BufferedReader(new InputStreamReader(rootStream));

            site.createDirectory(sitePath);

            String line = reader.readLine();
            while (line != null) {
                if (line.contains("."))
                    site.createFile(sitePath + File.separator + line, loader.getResourceAsStream(resourceRoot + File.separatorChar + line));
                else {
                    generateSite(resourceRoot + File.separator + line, sitePath + File.separatorChar + line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    enum Options {
        DEFAULT,
        NO_CONFIG
    }
}
