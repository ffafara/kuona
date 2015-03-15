package kuona;

import kuona.stringtemplate.Template;
import org.apache.commons.lang3.tuple.Pair;
import org.stringtemplate.v4.ST;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static kuona.utils.ConsoleColors.blue;
import static kuona.utils.Utils.puts;

public class FileTemplate {

    public static final String TEMPLATES_PROJECT_PATH = "templates/project";
    private ST st;

    public FileTemplate(ST st) {

        this.st = st;
    }

    public static FileTemplate get(String templateName) {
        Template g = new Template(TEMPLATES_PROJECT_PATH);

        ST st = g.getInstanceOf(templateName);
        if (st == null) {
            throw new RuntimeException("Failed to load " + templateName);
        }
        return new FileTemplate(st);
    }

    public static Pair<String, Object> property(String key, Object value) {
        return Pair.of(key, value);
    }

    public FileTemplate with(Pair<String, Object> p) {
        st.add(p.getKey(), p.getValue());
        return this;
    }

    public void renderTo(String filepath) {
        puts("Updating chart data " + blue(filepath));

        try {

            Writer writer = new OutputStreamWriter(new FileOutputStream(filepath), Charset.forName("UTF-8").newEncoder());

            writer.write(st.render());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file " + filepath);
        }

    }
}
