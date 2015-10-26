package kuona.web;

public class ElasticSearchConfig {
    private String host;
    private String port;

    public ElasticSearchConfig(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getAdress() { return this.getHost()+":"+this.getPort(); }
}
