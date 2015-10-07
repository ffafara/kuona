package tese.web.controllers;

import spark.Request;
import spark.Response;

public class ProjectMavenPomController {
    public Object get(Request request, Response response) throws Exception {
        return request.params();
    }

    public Object post(Request request, Response response) throws Exception {
        return request.params();
    }
}
