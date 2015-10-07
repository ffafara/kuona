package kuona;

import kuona.config.ApplicationConfigurationReader;
import kuona.config.KuonaSpec;
import kuona.controller.SiteUpdate;
import kuona.generator.Site;
import kuona.generator.SiteGenerator;
import kuona.server.KuonaServer;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.util.List;

import static kuona.utils.ConsoleColors.*;
import static kuona.utils.Utils.puts;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.StdSchedulerFactory.getDefaultScheduler;
import static spark.Spark.get;
import static spark.SparkBase.staticFileLocation;

public class Application {

    public static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        staticFileLocation("/site");

        startRefreher();

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch_graham").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));


        seedTestData(client);


        get("/", (req, resp) -> {
            GetResponse response = client.prepareGet("kuona", "config", "1")
                    .execute()
                    .actionGet();
            return response.getSourceAsString();
        });
        get("/hello", (req, res) -> "Hello World");

        get("/admin", (rq, rs) -> {
            GetResponse response = client.prepareGet("kuona", "config", "1")
                    .execute()
                    .actionGet();
            return new ModelAndView(response.getSource(), "admin.mustache");
        }, new MustacheTemplateEngine());
    }

    private static void seedTestData(Client client) {
        try {
            final XContentBuilder builder = jsonBuilder().startObject()
                    .field("site", "test site")
                    .startArray("projects")
                    .startObject()
                    .field("name", "Personal")
                    .startObject("ci-server")
                    .field("name", "Personal build server")
                    .field("url", "http://build.codekata.ninja")
                    .field("processor", "Jenkins")
                    .field("username", "graham")
                    .field("password", "5e26f32163a79007a9c9ebfecd28fde2")
                    .endObject()
                    .endObject()
                    .endArray()
                    .endObject();

            client.prepareIndex("kuona", "config", "1")
                    .setSource(builder)
                    .execute()
                    .actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startRefreher() {
        try {
            final Scheduler scheduler = getDefaultScheduler();

            JobDetail job = newJob(DataRefresher.class)
                    .withIdentity("job1", "group1")
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("DataRefresh", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(40)
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

//    public void run(String[] args) {
//        if (args.length < 1) {
//            if (configExists()) {
//                updateSite();
//            } else {
//                usage();
//            }
//        } else {
//            List<String> arguments = new ArrayList<>(Arrays.asList(args));
//
//            arguments.remove(0);
//
//            switch (args[0].toLowerCase()) {
//                case "create":
//                    createSite(arguments);
//                    break;
//                case "serve":
//                    startServer(arguments);
//                    break;
//                case "update":
//                    if (configExists()) {
//                        updateSite();
//                    } else {
//                        System.err.println("Configuration file " + ApplicationConfigurationReader.FILENAME + " not found");
//                    }
//                    break;
//                case "help":
//                    usage();
//                    break;
//                default:
//                    System.err.println("\n" + red("Error: ") + "Unrecognised command " + red(args[0]) + "\n");
//                    System.err.println("kuona help for usage instructions");
//                    break;
//            }
//        }
//    }

    private void startServer(List<String> arguments) {
        ApplicationConfigurationReader reader = new ApplicationConfigurationReader();
        KuonaSpec config = reader.read();

        String sitePath = config.getSitePath();

        KuonaServer server = new KuonaServer(sitePath);

        server.start();
    }


    private void updateSite() {
        ApplicationConfigurationReader reader = new ApplicationConfigurationReader();
        KuonaSpec config = reader.read();
        SiteUpdate update = new SiteUpdate(config);

        update.update();
    }

    private boolean configExists() {

        ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();

        return configurationReader.exists();
    }

    private void createSite(List<String> arguments) {

        SiteGenerator generator = new SiteGenerator(new Site(true));

        if (arguments.size() == 0) {
            System.err.println("\n" + red("Error: ") + "Missing site name for create\n");
            System.err.println("kuona help for usage instructions");
        } else {
            generator.generate("site", arguments.get(0));
        }
    }

    private void usage() {
        puts(red("kuona") + " version " + green(VERSION) + "\n" +
                yellow("Usage:\n") +
                blue("    kuona") + " <command> [command-args]\n" +
                "\n" +
                "kuona run in a project folder without any parameters updates the project data by reading from the configured CI systems.\n" +
                "\n" +
                yellow("Commands:\n") +
                "\n" +
                blue("create") + " name    Create a new projects in the named directory. Once created you can update the config.yml\n" +
                "               file with the required CI settings.\n" +
                blue("serve") + "          run the server on port 8080\n" +
                blue("update") + "         update the site using configuration from config.yml\n" +
                blue("help") + "           show this help\n" +
                "[no-args]      runs update if there is a config.yml file in the current working directory.\n" +
                "\n");
    }
}
