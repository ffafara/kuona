package kuona.gocd.analyser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Executor;

import java.io.IOException;
import java.io.Serializable;

import static org.apache.http.client.fluent.Request.Get;

public class MetricConfig implements Serializable {

    private GoCdConfig goCdConfig;
    private String metricType;
    private String config;

    /**
     * Retrieve metric configuration from Kuona Web App
     *
     * @param kuonaAppConfig    Location of the kuona web app and name of the metric
     */
    public MetricConfig(KuonaAppConfig kuonaAppConfig) {
        this(kuonaAppConfig, Executor.newInstance());
    }

    public MetricConfig(KuonaAppConfig kuonaAppConfig, Executor executor) {
        try {
            String stringResponse = executor.execute(Get(kuonaAppConfig.getKuonaURL()))
                    .returnContent()
                    .asString();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(stringResponse).getAsJsonObject();

            kuonaAppConfig.setMetricURL(jsonObject.get("metricURL").getAsString());
            kuonaAppConfig.setRawDataURL(jsonObject.get("rawDataURL").getAsString());

            JsonObject goCdJson = jsonObject.getAsJsonObject("gocd");
            this.goCdConfig = new GoCdConfig(
                    goCdJson.get("url").getAsString(),
                    goCdJson.get("user").getAsString(),
                    goCdJson.get("password").getAsString()
            );

            JsonObject metricJson = jsonObject.getAsJsonObject("metric");
            this.metricType = metricJson.get("type").getAsString();
            this.config = metricJson.get("config").getAsJsonObject().toString();

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public GoCdConfig getGoCdConfig() {
        return goCdConfig;
    }

    public String getMetricType() {
        return metricType;
    }

    public String getConfig() {
        return config;
    }
}

