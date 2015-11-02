package kuona.java.analyser.model;

import com.google.gson.Gson;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JavaSourceFileTest {
    @Test
    public void isSserializable() {
        Gson gson = new Gson();

        final String json = gson.toJson(new JavaSourceFile(Paths.get("."), "some package", null));

        assertThat(json, is("{\"path\":\"\",\"packageName\":\"some package\"}"));
    }


}