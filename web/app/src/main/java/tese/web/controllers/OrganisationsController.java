package tese.web.controllers;

import spark.Request;
import spark.Response;

import java.util.HashMap;

public class OrganisationsController {
    public Object list(Request request, Response response) {
        return new HashMap<String, String>();
    }

    public Object get(Request request, Response response) {
        return request.params();
    }

    public Object post(Request request, Response response) {
        return new HashMap<String, String>();
    }
}
