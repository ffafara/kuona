package kuona.web.controllers;

import com.google.gson.Gson;
import kuona.web.Repository;
import spark.Request;
import spark.Response;

public class MetricsController {

    private Repository repository;
    private Gson gson;

    public MetricsController(Repository repository) {
        this.repository = repository;
        this.gson = new Gson();
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

    public Object saveData(Request request, Response response) {

        String metric = request.params(":metric");
        String data = request.body();
        repository.saveMetricData(metric, data);
        response.status(201);
        return null;
    }

    public Object getMetric(Request request, Response response) {

        String metric = request.params(":metric");
        return repository.getMetric(metric);
    }
}
