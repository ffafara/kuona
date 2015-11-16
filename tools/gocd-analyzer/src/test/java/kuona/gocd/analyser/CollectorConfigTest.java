package kuona.gocd.analyser;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.apache.http.client.fluent.Request.Get;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollectorConfigTest {

    Executor mockExecutor;

    @Before
    public void setUp() throws Exception {
        final String mockConfig = "{\n" +
                "\"metricURL\": \"http://localhost/metric/test-name\",\n" +
                "\"rawDataURL\": \"http://localhost/raw-data/test-name\",\n" +
                "\"url\": \"http://somehost.com\",\n" +
                "\"user\": \"someuser\",\n" +
                "\"password\": \"somepass\",\n" +
                "\"type\": \"GoNoGo\",\n" +
                "\"frequency\": \"30\",\n" +
                "\"config\": {\"testkey\": \"testvalue\"}\n" +
                "}";

        Content mockContent = mock(Content.class);
        when(mockContent.asString()).thenReturn(mockConfig);

        Response mockRepsonse = mock(Response.class);
        when(mockRepsonse.returnContent()).thenReturn(mockContent);

        mockExecutor = mock(Executor.class);
        when(mockExecutor.execute(any())).thenReturn(mockRepsonse);
    }

    @Test
    public void shouldDoGetRequestToKuonaUrl() throws IOException {
        CollectorConfig testCollectorConfig = new CollectorConfig("http://localhost", "test-name");
        testCollectorConfig.fetch(mockExecutor);

        verify(mockExecutor).execute(argThat(new KuonaGetRequestMatcher(testCollectorConfig.getKuonaURL())));
    }

    @Test
    public void shouldParseMetricConfigurationFromKuona() throws IOException, URISyntaxException {
        CollectorConfig testCollectorConfig = new CollectorConfig("http://localhost", "test-name");
        testCollectorConfig.fetch(mockExecutor);

        assertThat(testCollectorConfig.getMetricConfig(), is("{\"testkey\":\"testvalue\"}"));
        assertThat(testCollectorConfig.getMetricType(), is("GoNoGo"));
    }

    class KuonaGetRequestMatcher extends ArgumentMatcher<Request> {
        String url;

        public KuonaGetRequestMatcher(String url) {
            this.url = url;
        }

        public boolean matches(Object req) {
            return req.toString().equals(Get(url).toString());
        }
    }

}