package kuona.gocd.analyser.metric;

import kuona.gocd.analyser.KuonaAppConfig;

public interface Metric {

    void analyze(String rawData);

    void setKuonaAppConfig(KuonaAppConfig kuonaAppConfig);

    void setMetricConfig(String config);
}
