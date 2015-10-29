package kuona.snapci.analyser.metric;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kuona.snapci.analyser.KuonaAppConfig;
import kuona.snapci.analyser.model.Pipeline;
import kuona.snapci.analyser.model.Stage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GoNoGoTest {
    private List<Stage> testStages;
    private Pipeline testPipeline;
    private KuonaAppConfig testKuonaAppConfig;

    @Before
    public void setUp() throws Exception {
        testStages = new ArrayList<>();

        final Stage testStage1 = new Stage();
        testStage1.setName("Dummy Stage #1");
        testStage1.setResult("success");
        testStages.add(testStage1);

        final Stage testStage2 = new Stage();
        testStage2.setName("Dummy Stage #2");
        testStage2.setResult("failed");
        testStages.add(testStage2);

        final Stage testStage3 = new Stage();
        testStage3.setName("Dummy Stage #3");
        testStage3.setResult("building");
        testStages.add(testStage3);

        testPipeline = new Pipeline();
        testPipeline.setStages(testStages);

        testKuonaAppConfig = new KuonaAppConfig("", "test-gonogo-metric");
    }

    @Test
    public void shouldReadConfiguration() {
        final String testConfig = "{\"stages\": [\"Dummy Stage #1\", \"Dummy Stage #3\"]}";
        final GoNoGo testGoNoGo = new GoNoGo();
        testGoNoGo.setMetricConfig(testConfig);

        List<String> expected = Arrays.asList("Dummy Stage #1", "Dummy Stage #3");

        assertThat(testGoNoGo.getConfig().getStages(), is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionAtEmptyConfigurationString() {
        final String testConfig = null;
        final GoNoGo testGoNoGo = new GoNoGo();
        testGoNoGo.setMetricConfig(testConfig);
    }

    @Test
    public void shouldAcceptJsonStringWithMissingConfigurationKey() {
        final String testConfig = "{\"foo\": \"bar\"}";
        final GoNoGo testGoNoGo = new GoNoGo();
        testGoNoGo.setMetricConfig(testConfig);

        List<String> expected = null;

        assertThat(testGoNoGo.getConfig().getStages(), is(expected));
    }

    @Test
    public void shouldPrepareJsonResponseWithMetricMetadata() {
        final String testConfig = "{\"stages\": [\"Dummy Stage #1\",\"Dummy Stage #2\", \"Dummy Stage #3\"]}";
        final GoNoGo goNoGo = new GoNoGo();
        goNoGo.setMetricConfig(testConfig);
        goNoGo.setKuonaAppConfig(testKuonaAppConfig);

        final String expected = "\"metric\":\"test-gonogo-metric\",\"metricType\":\"GoNoGo\"";
        String actual = goNoGo.prepareMetric(testPipeline);
        JsonParser jsonParser = new JsonParser();
        JsonElement actualJson = jsonParser.parse(actual);

        assertTrue(actualJson.isJsonObject());
        assertTrue(actualJson.getAsJsonObject().has("timestamp"));
        assertThat(actual, containsString(expected));
    }

    @Test
    public void shouldCalculateMetricForFailedStage() {
        final String testConfig = "{\"stages\": [\"Dummy Stage #1\",\"Dummy Stage #2\", \"Dummy Stage #3\"]}";
        final GoNoGo goNoGo = new GoNoGo();
        goNoGo.setMetricConfig(testConfig);
        goNoGo.setKuonaAppConfig(testKuonaAppConfig);

        String actual = goNoGo.prepareMetric(testPipeline);

        assertThat(actual, containsString("\"status\":\"red\""));
    }

    @Test
    public void shouldCalculateMetricForBuildingStage() {
        final String testConfig = "{\"stages\": [\"Dummy Stage #1\", \"Dummy Stage #3\"]}";
        final GoNoGo goNoGo = new GoNoGo();
        goNoGo.setMetricConfig(testConfig);
        goNoGo.setKuonaAppConfig(testKuonaAppConfig);

        String actual = goNoGo.prepareMetric(testPipeline);

        assertThat(actual, containsString("\"status\":\"building\""));
    }

    @Test
    public void shouldCalculateMetricForPassingStages() {
        final String testConfig = "{\"stages\": [\"Dummy Stage #1\"]}";
        final GoNoGo goNoGo = new GoNoGo();
        goNoGo.setMetricConfig(testConfig);
        goNoGo.setKuonaAppConfig(testKuonaAppConfig);

        String actual = goNoGo.prepareMetric(testPipeline);

        assertThat(actual, containsString("\"status\":\"green\""));
    }

}