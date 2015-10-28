package kuona.snapci.analyser.metric;

import kuona.snapci.analyser.KuonaAppConfig;

public interface Metric {

    void analyze(String rawData);

    void setKuonaAppConfig(KuonaAppConfig kuonaAppConfig);

    void setMetricConfig(String config);
}
