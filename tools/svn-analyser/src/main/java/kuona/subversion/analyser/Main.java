package kuona.subversion.analyser;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("Kuona Subversion analyser");

        SubversionProcessor processor = new SubversionProcessor(parseOptions(args));

        processor.analyse();
    }

    protected static void printHelp(OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(writer, 80, "java -jar kuona-svn-analyser.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }

    static RepositorySpec parseOptions(String[] args) {
        final RepositorySpec result = new RepositorySpec();
        try {
            Options options = commandLineOptions();


            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('s')) {
                result.setUrl(cmd.getOptionValue('s'));
            }

            if (cmd.hasOption('h')) {
                printHelp(System.out);
            }

            result.setRoots(cmd.getArgList());
        } catch (MissingOptionException moe) {
            printHelp(System.out);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Options commandLineOptions() {
        Options options = new Options();

        final Option serverOption = new Option("s", "url", true, "URL subversion server");
        serverOption.setRequired(true);
        options.addOption(serverOption);
        options.addOption(new Option("h", "help", false, "Output this message"));
        return options;
    }
}
