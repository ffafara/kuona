package kuona.gocd.analyser;

import java.io.Serializable;

public class KuonaAppConfig implements Serializable {
    private String kuonaURL, metricName;
    private String rawDataURL, metricURL;

    public KuonaAppConfig(String kuonaURL, String metricName) {
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
}
