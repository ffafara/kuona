package kuona.web;

import com.google.gson.Gson;
import kuona.web.controllers.HomeController;
import kuona.web.controllers.OrganisationsController;
import kuona.web.controllers.ProjectMavenPomController;
import kuona.web.controllers.ProjectsController;
import org.apache.commons.cli.*;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.OutputStream;
import java.io.PrintWriter;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        final CommandLine commandLine = parseOptions(args);

        port(Integer.parseInt(commandLine.getOptionValue('p', "9000")));

        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_graham").build();

        final Repository repository = new Repository(settings);


        Gson gson = new Gson();

        webSocket("/app/status", StatusSocket.class);

        staticFileLocation("/public");

        get("/settings", new HomeController(repository), gson::toJson);

        final ProjectsController projectsController = new ProjectsController(repository);
        get("/app/projects", projectsController::list, gson::toJson);
        post("/app/projects", projectsController::create, gson::toJson);

        final OrganisationsController organisationsController = new OrganisationsController();
        get("/orgs", organisationsController::list, gson::toJson);
        get("/orgs/:org", organisationsController::get, gson::toJson);
        post("/orgs", organisationsController::post, gson::toJson);



        final ProjectMavenPomController mavenPomController = new ProjectMavenPomController();
        get("/orgs/:org/projects/:project/metrics/java/maven/dependencies", mavenPomController::get, gson::toJson);
        post("/orgs/:org/projects/:project/metrics/java/maven/dependencies", mavenPomController::post, gson::toJson);
    }

    protected static CommandLine parseOptions(String[] args) {
        try {
            Options options = commandLineOptions();


            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static void printHelp(OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(writer, 80, "java -jar kuona-maven-analyser.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }

    protected static Options commandLineOptions() {
        Options options = new Options();

        options.addOption(new Option("p", "port", true, "Server port for HTTP traffic (default 9000)"));
        options.addOption(new Option("h", "help", false, "Output this message"));
        return options;
    }

}
