package kuona.web.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MetricConfig {
    String name;
    String description;
    String type;
    int frequency;
    JsonObject config;
    BuildServer buildServer;
    String url;
    String user;
    String password;
    String metricURL;
    String rawDataURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfig() {
        return config.toString();
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public BuildServer getBuildServer() {
        return buildServer;
    }

    public void setBuildServer(BuildServer buildServer) {
        this.buildServer = buildServer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getMetricURL() {
        return metricURL;
    }

    public String getRawDataURL() {
        return rawDataURL;
    }

    public void generateUrls() {
        this.metricURL = "http://localhost:9000/metrics";
        this.rawDataURL = "http://localhost:9000/metrics/"+this.name+"/rawdata";
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}


