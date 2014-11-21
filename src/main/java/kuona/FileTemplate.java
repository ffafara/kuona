package kuona;

import org.apache.commons.lang3.tuple.Pair;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;

import java.io.FileWriter;
import java.io.IOException;

import static kuona.utils.Utils.puts;

public class FileTemplate {

    public static final String TEMPLATES_PROJECT_PATH = "templates/project/";
    private ST st;

    public FileTemplate(ST st) {

        this.st = st;
    }

    public static FileTemplate get(String templateName) {
        STGroup g = new STRawGroupDir(TEMPLATES_PROJECT_PATH);

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
        puts("Updating chart data " + filepath);

        try {
            FileWriter activityChartFile = new FileWriter(filepath);
            activityChartFile.write(st.render());
            activityChartFile.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file " + filepath);
        }

    }
}
