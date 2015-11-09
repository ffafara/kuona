package kuona.jenkins.analyser;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        System.out.println("Kuona jenkins build analyser");
        final BuildServerSpec buildServerSpec = parseOptions(args);

        final JenkinsProcessor jenkinsProcessor = new JenkinsProcessor(buildServerSpec);

        final BuildMetrics buildMetrics = new BuildMetrics();
        jenkinsProcessor.collectMetrics(buildMetrics);


    }

    protected static void printHelp(OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(writer, 80, "java -jar kuona-maven-analyser.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }

    static BuildServerSpec parseOptions(String[] args) {
        final BuildServerSpec result = new BuildServerSpec();
        try {
            Options options = commandLineOptions();


            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

//            result.setArgs(cmd.getArgs());

            if (cmd.hasOption("s")) {
                result.setServer(cmd.getOptionValue("s"));
            }
            if (cmd.hasOption("u")) {
                result.setUsername(cmd.getOptionValue("u"));
            }

            if (cmd.hasOption("p")) {
                result.setPassword(cmd.getOptionValue("p"));
            }

            if (cmd.hasOption("h") || cmd.getArgList().size() == 0) {
                printHelp(System.out);
            }

        } catch (ParseException e) {
            System.err.println("Error " + e.getMessage());
            printHelp(System.out);
        }
        return result;
    }

    private static Options commandLineOptions() {
        Options options = new Options();

        final Option serverOption = new Option("s", "server", true, "URL of the build server");
        serverOption.setRequired(true);
        options.addOption(serverOption);
        options.addOption(new Option("u", "username", true, "Usename to access build server"));
        options.addOption(new Option("p", "password", true, "password for build server"));
        options.addOption(new Option("h", "help", false, "Output this message"));
        return options;
    }
}
