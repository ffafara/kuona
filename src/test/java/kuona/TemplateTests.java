package kuona;


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
    public void handleMapData() throws IOException {

        HashMap node = new HashMap() {{
            put("value", "foobar");
        }};

        ST template = new ST("<dashboard.value>");
        template.add("dashboard", node);

        assertThat(template.render(), is("foobar"));

    }

}
