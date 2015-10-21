package kuona.web.controllers;

import kuona.web.Repository;
import kuona.web.model.Project;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Map;

public class ProjectsController {
    private Repository repository;

    public ProjectsController(Repository repository) {

        this.repository = repository;
    }

    public Object list(Request request, Response response) throws Exception {
        final ArrayList<Object> projects = new ArrayList<>();
        projects.add(new Project("foo", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        return projects;
    }

    public Object create(Request request, Response response) throws Exception {
        final Map<String, String[]> project = request.queryMap("project").toMap();

        Project p = Project.fromMap(project);

        repository.save(p);

        response.redirect("/dashboard");
        return null;
    }

}
