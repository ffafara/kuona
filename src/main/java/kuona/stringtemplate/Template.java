package kuona.stringtemplate;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Template {

    private String directoryName;

    public Template(String directoryName) {
        this.directoryName = directoryName;
    }

    public ST getInstanceOf(String name) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String path = directoryName + File.separatorChar + name + ".st";
        final InputStream stream = cl.getResourceAsStream(path);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(stream, writer, "UTF-8");

            return new ST(writer.toString());
        } catch (IOException e) {
            System.err.println("Failed to create template");
            throw new RuntimeException(e);
        }
    }
}
