package kuona.snapci.analyser;

import java.io.Serializable;

public class ElasticSearchConfig implements Serializable {
    private String clusterName, host;
    private int port;

    public ElasticSearchConfig(String clusterName, String host, Integer port) {
        this.clusterName = clusterName;
        this.host = host;
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
