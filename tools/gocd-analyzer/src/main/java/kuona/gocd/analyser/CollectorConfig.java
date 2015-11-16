package kuona.gocd.analyser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Executor;

import java.io.IOException;
import java.io.Serializable;

import static org.apache.http.client.fluent.Request.Get;

public class CollectorConfig implements Serializable {
    private String kuonaURL, metricName;
    private String rawDataURL, metricURL;
    private GoCdConfig goCdConfig;
    private String metricType;
    private String metricConfig;
    private int frequency;


    public CollectorConfig(String kuonaURL, String metricName) {
        this.kuonaURL = kuonaURL;
        this.metricName = metricName;
    }

    public String getKuonaURL() {
        return kuonaURL;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getRawDataURL() {
        return rawDataURL;
    }

    public String getMetricURL() {
        return metricURL;
    }

    public void setRawDataURL(String rawDataURL) {
        this.rawDataURL = rawDataURL;
    }

    public void setMetricURL(String metricURL) {
        this.metricURL = metricURL;
    }

    public GoCdConfig getGoCdConfig() {
        return goCdConfig;
    }

    public String getMetricType() {
        return metricType;
    }

    public String getMetricConfig() {
        return metricConfig;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * Retrieve collector and metric configuration from Kuona
     */
    public void fetch() {
        fetch(Executor.newInstance());
    }

    protected void fetch(Executor executor) {
        try {
            String stringResponse = executor.execute(Get(kuonaURL))
                    .returnContent()
                    .asString();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(stringResponse).getAsJsonObject();

            setMetricURL(jsonObject.get("metricURL").getAsString());
            setRawDataURL(jsonObject.get("rawDataURL").getAsString());

            this.goCdConfig = new GoCdConfig(
                    jsonObject.get("url").getAsString(),
                    jsonObject.get("user").getAsString(),
                    jsonObject.get("password").getAsString()
            );

            this.metricType = jsonObject.get("type").getAsString();
            this.frequency = jsonObject.get("frequency").getAsInt();
            this.metricConfig = jsonObject.get("config").getAsJsonObject().toString();

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to fetch or read configuration");
        }
    }


}
