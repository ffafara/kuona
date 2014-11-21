package kuona;

import kuona.config.ApplicationConfiguration;
import kuona.config.ApplicationConfigurationReader;
import kuona.config.KuonaSpec;
import kuona.controller.SiteUpdate;
import kuona.generator.Site;
import kuona.generator.SiteGenerator;
import kuona.server.KuonaServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kuona.utils.Utils.puts;

public class Application {

    public static final String VERSION = "0.0.1";

    public void run(String[] args) {
        if (args.length < 1) {
            if (configExists()) {
                updateSite();
            } else {
                usage();
            }
        } else {
            List<String> arguments = new ArrayList<>(Arrays.asList(args));

            arguments.remove(0);

            switch (args[0].toLowerCase()) {
                case "create":
                    createSite(arguments);
                    break;
                case "serve":
                    startServer(arguments);
                    break;
                case "update":
                    if (configExists()) {
                        updateSite();
                    } else {
                        System.err.println("Configuration file " + ApplicationConfigurationReader.FILENAME + " not found");
                    }
                    break;
                default:
                    System.err.println("Unrecognised command " + args[0] + "\n");
                    usage();
                    break;
            }
        }
    }

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

        generator.generate("site", arguments.get(0));
    }

    private void usage() {
        puts("kuona version " + VERSION + "\n" +
                "Usage:\n" +
                "kuona <command> [command-args]\n" +
                "\n" +
                "kuona run in a project folder without any parameters updates the project data by reading from the configured CI systems.\n" +
                "\n" +
                "Commands:\n" +
                "\n" +
                "create name    Create a new projects in the named directory. Once created you can update the config.yml\n" +
                "               file with the required CI settings.\n" +
                "\n");
    }
}
