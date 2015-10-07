package tese.web;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import tese.web.controllers.HomeController;
import tese.web.controllers.OrganisationsController;
import tese.web.controllers.ProjectMavenPomController;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application {
    public static void main(String[] args) {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_graham").build();
        Gson gson = new Gson();

        get("/", new HomeController(settings), gson::toJson);

        final OrganisationsController organisationsController = new OrganisationsController();
        get("/orgs", organisationsController::list, gson::toJson);
        get("/orgs/:org", organisationsController::get, gson::toJson);
        post("/orgs", organisationsController::post, gson::toJson);

        final ProjectMavenPomController mavenPomController = new ProjectMavenPomController();
        get("/orgs/:org/projects/:project/metrics/java/maven/dependencies", mavenPomController::get, gson::toJson);
        post("/orgs/:org/projects/:project/metrics/java/maven/dependencies", mavenPomController::post, gson::toJson);
    }
}
