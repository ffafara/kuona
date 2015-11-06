package kuona.web.controllers;

import com.google.gson.Gson;
import kuona.web.Repository;
import kuona.web.model.Metric;
import kuona.web.response.MetricsResponse;
import spark.Request;
import spark.Response;
import java.util.HashMap;

public class MetricsController {
    private Repository repository;

    public MetricsController(Repository repository) {
        this.repository = repository;
    }

    public Object create(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        final HashMap hashMap = gson.fromJson(request.body(), HashMap.class);

        System.out.println(request.body());

        final Metric metric = new Metric(hashMap.get("timestamp").toString(), hashMap.get("name").toString(), request.body().getBytes());

        repository.save(metric);

        response.status(200);

        return new MetricsResponse("created");
    }

    public Object getConfig(Request request, Response response) {

        String metric = request.params(":metric");
        return repository.getMetricConfig(metric);
    }

    public Object saveRawData(Request request, Response response) {

        String metric = request.params(":metric");
        String data = request.body();
        repository.saveMetricRawData(metric, data);
        response.status(201);
        return null;
    }

    public Object getMetric(Request request, Response response) {

        String metric = request.params(":metric");
        return repository.getMetric(metric);
    }

}
