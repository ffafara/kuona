package kuona.web.controllers;

import kuona.web.Repository;
import spark.Request;
import spark.Response;
import spark.Route;


public class HomeController implements Route {
    private Repository repository;

    public HomeController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        return repository.getConfig();
    }
}
