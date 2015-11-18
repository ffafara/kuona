package kuona.web.controllers;

import com.google.gson.Gson;
import kuona.web.Repository;
import kuona.web.model.MetricConfig;
import kuona.web.response.MetricsResponse;
import spark.Request;
import spark.Response;

public class MetricConfigsController {

    private Repository repository;

    public MetricConfigsController(Repository repository) {
        this.repository = repository;
    }

    public Object list(Request request, Response response) {
        return repository.getMetricConfigList();
    }

    public Object get(Request request, Response response) {

        String metric = request.params(":metric");
        return repository.getMetricConfig(metric);
    }

    public Object create(Request request, Response response) {
        Gson gson = new Gson();
        final MetricConfig metricConfig = gson.fromJson(request.body(), MetricConfig.class);
        metricConfig.generateUrls();

        repository.save(metricConfig);

        response.status(200);
        return new MetricsResponse("created");
    }

}
