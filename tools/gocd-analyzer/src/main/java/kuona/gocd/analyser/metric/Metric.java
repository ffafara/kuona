package kuona.gocd.analyser.metric;

import kuona.gocd.analyser.CollectorConfig;

public interface Metric {

    void analyze(String rawData);

    void setCollectorConfig(CollectorConfig collectorConfig);

    void setMetricConfig(String config);
}
