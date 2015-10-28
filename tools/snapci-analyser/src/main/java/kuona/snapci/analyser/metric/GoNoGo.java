package kuona.snapci.analyser.metric;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kuona.snapci.analyser.KuonaAppConfig;
import kuona.snapci.analyser.model.Pipeline;
import kuona.snapci.analyser.model.Stage;
import kuona.snapci.analyser.utils.Deserializer;
import org.apache.http.client.fluent.Executor;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.client.fluent.Request.Post;

public class GoNoGo implements Metric {
    private KuonaAppConfig kuonaAppConfig;
    private String configJson;


    @Override
    public void setKuonaAppConfig(KuonaAppConfig kuonaAppConfig) {
        this.kuonaAppConfig = kuonaAppConfig;
    }

    @Override
    public void setMetricConfig(String config) {
        this.configJson = config;
    }

    @Override
    public void analyze(String rawData) {
        saveRawData(rawData);

        // Parse json to class
        Pipeline pipeline = new Deserializer().objectFromString(Pipeline.class, rawData);

        saveMetricData(prepareMetric(pipeline));
    }

    private void saveRawData(String data) {
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

    private String prepareMetric(Pipeline pipeline) {
        List<Stage> validStages;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement metricStagesJson = jsonParser.parse(configJson).getAsJsonObject().get("stages").getAsJsonObject();
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> metricStages = gson.fromJson(metricStagesJson, type);
            validStages = pipeline.getStages().stream()
                    .filter(s -> metricStages.contains(s.getName())).collect(Collectors.toList());
        } catch (JsonParseException e) {
            validStages = pipeline.getStages();
        }

        long red = validStages.stream().filter(s -> s.getResult().equals("failed")).count();
        long building = validStages.stream().filter(s -> s.getResult().equals("building")).count();
        long green = validStages.stream().filter(s -> s.getResult().equals("success")).count();

        JsonObject result = new JsonObject();
        result.addProperty("timestamp", new Date().getTime());
        result.addProperty("metric", kuonaAppConfig.getMetricName());
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

// Write stages info to ElasticSearch
//        pipeline.getStages().forEach(stage -> {
//            try {
//                elasticClient.prepareIndex(KUONA_METRICS_INDEX, "stagebuild")
//                        .setSource(jsonBuilder()
//                                        .startObject()
//                                        .field("date", new Date())
//                                        .field("name", stage.getName())
//                                        .field("project", stage.getProjectName())
//                                        .field("result", stage.getResult())
//                                        .endObject()
//                        )
//                        .execute();
//            } catch (IOException e) {
//                Utils.puts(e.toString());
//                e.printStackTrace();
//            }
//        });
//
//        // Write pipeline info to ElasticSearch
//        elasticClient.prepareIndex("kuona-metrics", "pipelinebuilstatus")
//                .setSource(jsonBuilder()
//                                .startObject()
//                                .field("name", "Palace-Intrigue")
//                                .field("buildserver", "snap")
//                                .field("status", pipeline.getResult())
//                                .endObject()
//                )
//                .execute();