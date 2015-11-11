package kuona.gocd.analyser.metric;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kuona.gocd.analyser.KuonaAppConfig;
import kuona.gocd.analyser.model.Pipeline;
import kuona.gocd.analyser.model.PipelineHistory;
import kuona.gocd.analyser.model.Result;
import kuona.gocd.analyser.model.Stage;
import org.apache.http.client.fluent.Executor;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.client.fluent.Request.Post;

public class GoNoGo implements Metric {
    private KuonaAppConfig kuonaAppConfig;
    private GoNoGoConfig config;


    @Override
    public void setKuonaAppConfig(KuonaAppConfig kuonaAppConfig) {
        this.kuonaAppConfig = kuonaAppConfig;
    }

    @Override
    public void setMetricConfig(String rawConfig) throws IllegalArgumentException {
        try {
            Gson gson = new Gson();
            this.config = gson.fromJson(rawConfig, GoNoGoConfig.class);
            if (this.config == null)
                throw new IllegalArgumentException("Cannot parse metric config");
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Cannot parse metric config");
        }
    }

    public GoNoGoConfig getConfig() {
        return config;
    }

    @Override
    public void analyze(String rawData) {
        saveRawData(rawData);

        Pipeline pipeline = parse(rawData);
        if (pipeline != null) {
            saveMetricData(prepareMetric(pipeline));
        }
    }

    protected Pipeline parse(String rawData) {
        try {
            Gson gson = new Gson();
            PipelineHistory pipelineHistory = gson.fromJson(rawData, PipelineHistory.class);
            return pipelineHistory.getPipelines().get(0);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse GoCd pipeline information: \n" + rawData);
        }
        return null;
    }

    protected void saveRawData(String data) {
        Executor executor = Executor.newInstance();
        try {
            executor.execute(Post(kuonaAppConfig.getRawDataURL())
                    .bodyString(data, ContentType.APPLICATION_JSON));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMetricData(String data) {
        Executor executor = Executor.newInstance();
        try {
            executor.execute(Post(kuonaAppConfig.getMetricURL())
                    .bodyString(data, ContentType.APPLICATION_JSON));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String prepareMetric(Pipeline pipeline) {
        List<Stage> validStages;
        List<String> configStages = config.getStages();
        if (configStages != null) {
            validStages = pipeline.getStages().stream()
                    .filter(s -> configStages.contains(s.getName())).collect(Collectors.toList());
        } else {
            validStages = pipeline.getStages();
        }

        long red = validStages.stream().filter(s -> s.getResult().equals(Result.Failed)).count();
        long building = validStages.stream().filter(s -> s.getResult().equals(Result.Rescheduled) || s.getResult().equals(Result.Cancelled) || s.getResult().equals(Result.Unknown)).count();
        long green = validStages.stream().filter(s -> s.getResult().equals(Result.Passed)).count();

        JsonObject result = new JsonObject();
        result.addProperty("timestamp", new Date().getTime());
        result.addProperty("name", kuonaAppConfig.getMetricName());
        result.addProperty("metricType", "GoNoGo");

        if (red > 0) {
            result.addProperty("status", "red");
        } else if (building > 0) {
            result.addProperty("status", "building");
        } else {
            result.addProperty("status", "green");
        }
        return result.toString();
    }
}
