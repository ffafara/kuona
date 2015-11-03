package kuona.web;

import com.google.gson.Gson;
import kuona.web.controllers.*;
import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        final CommandLine commandLine = parseOptions(args);

        if (commandLine.hasOption('h')) {
            printHelp(System.out);
            return;
        }

        port(Integer.parseInt(commandLine.getOptionValue('p', "9000")));

        ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig(
                commandLine.getOptionValue("eh", "127.0.0.1"),
                commandLine.getOptionValue("ep", "9200")
        );

        String[] elasticSearchHosts = {"localhost:9300"};
        if (commandLine.hasOption('e')) {
            elasticSearchHosts = commandLine.getOptionValues('e');
        }

        final Repository repository = new Repository(elasticSearchHosts);


        Gson gson = new Gson();

        webSocket("/app/status", StatusSocket.class);

        staticFileLocation("/public");

        get("/settings", new HomeController(repository), gson::toJson);

        final MetricsController metricsController = new MetricsController(repository);
        post("/metrics", metricsController::create, gson::toJson);

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

        final GoNoGoMetricController goNoGoMetricController = new GoNoGoMetricController(elasticSearchConfig);
        get("/project/:project/metric/gonogo", goNoGoMetricController::get, gson::toJson);

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
        formatter.printHelp(writer, 80, "java -jar web-app.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }

    protected static Options commandLineOptions() {
        Options options = new Options();

        options.addOption(new Option("p", "port", true, "Server port for HTTP traffic (default 9000)"));
        options.addOption(new Option("h", "help", false, "Output this message"));
        options.addOption(new Option("e", "elastic", true, "Elastic search API end point. For a cluster define a parameter for each"));
        options.addOption(new Option("ep", "elasticport", true, "Server port for ElasticSearch (default 9200)"));
        options.addOption(new Option("eh", "elastichost", true, "Hostname / IP address of ElasticSearch (default 127.0.0.1)"));
        return options;
    }

}
