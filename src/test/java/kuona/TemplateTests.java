package kuona;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TemplateTests {
    @Test
    public void ampersandDelimitors() {
        ST template = new ST("$message$", '$', '$');
        template.add("message", "Graham was here");

        assertThat(template.render(), is("Graham was here"));
    }

    @Test
    @Ignore
    public void handleJsonData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"value\": \"foobar\"}");

        ST template = new ST("<dashboard.map.(value)>");
        template.add("dashboard", node);

        assertThat(template.render(), is("foobar"));

    }

    @Test
    public void handleMapData() throws IOException {

        HashMap node = new HashMap() {{
            put("value", "foobar");
        }};

        ST template = new ST("<dashboard.value>");
        template.add("dashboard", node);

        assertThat(template.render(), is("foobar"));

    }

}
