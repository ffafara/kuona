package kuona.web.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kuona.web.ElasticSearchConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.http.client.fluent.Request.Post;

public class GoNoGoMetricController {

    private ElasticSearchConfig elasticSearchConfig;

    public GoNoGoMetricController(ElasticSearchConfig elasticSearchConfig) {
        this.elasticSearchConfig = elasticSearchConfig;
    }

    public Object get(Request request, Response response) {

        String project = request.params(":project");

        return prepareMetric(project);
    }

    private String getStageStatus(String stage, String project) {
        String query = "{\n" +
                "   \"query\": {\n" +
                "       \"bool\": {\n" +
                "           \"must\": [\n" +
                "               {\n" +
                "                  \"match_phrase\": {\n" +
                "                      \"name\": \""+ stage +"\"\n" +
                "                  }\n" +
                "               },\n" +
                "               {\n" +
                "                  \"match_phrase\": {\n" +
                "                       \"project\": \""+ project +"\"\n" +
                "                  }\n" +
                "               }\n" +
                "           ]\n"+
                "       }\n" +
                "   },\n" +
                "   \"size\": \"1\",\n" +
                "   \"sort\": [\n" +
                "       {\n" +
                "           \"_timestamp\": {\n" +
                "               \"order\": \"desc\"\n" +
                "           }\n" +
                "       }\n" +
                "   ]\n" +
                "}";

        Executor executor = Executor.newInstance();
        try {
            String stringResponse = executor.execute(Post("http://"+ elasticSearchConfig.getAdress() + "/kuona-metrics/stagebuild/_search")
                    .bodyString(query, ContentType.APPLICATION_JSON))
                    .returnContent().asString();
            JsonParser jsonParser = new JsonParser();
            String stringResult = jsonParser.parse(stringResponse).getAsJsonObject()
                    .get("hits").getAsJsonObject()
                    .getAsJsonArray("hits").get(0).getAsJsonObject()
                    .get("_source").getAsJsonObject()
                    .get("result").getAsString();
            return stringResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Object prepareMetric(String project) {
        String[] STAGES = new String[] {"Unit-tests", "Integration-tests"};

        List<String> statusList = new ArrayList<>();
        for (String stage : STAGES) {
            statusList.add(getStageStatus(stage, project));
        }

        long red = statusList.stream().filter(s -> s.equals("failed")).count();
        long building = statusList.stream().filter(s -> s.equals("building")).count();
        long green = statusList.stream().filter(s -> s.equals("success")).count();

        JsonObject result = new JsonObject();
        result.addProperty("project", project);
        result.addProperty("timestamp", new Date().getTime());
        result.addProperty("type", "singlevalue");
        result.addProperty("metric", "gonogo");

        if (red > 0) {
            result.addProperty("status", "red");
        } else if (building > 0) {
            result.addProperty("status", "building");
        } else {
            result.addProperty("status", "green");
        }
        return result;
    }

}
