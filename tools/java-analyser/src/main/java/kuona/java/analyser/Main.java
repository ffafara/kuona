package kuona.java.analyser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.gson.Gson;
import kuona.client.PostResults;
import kuona.java.analyser.model.JavaClassDeclaration;
import kuona.java.analyser.model.JavaSourceFile;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws Exception {

        try {
            final CommandLine options = parseOptions(args);

            if (options.hasOption('h')) {
                printHelp(System.out);
                return;
            }

            URL resultUrl = new URL(options.getOptionValue('r'));

            final String[] files = options.getArgs();

            List<JavaSourceFile> sources = new ArrayList<>();

            for (String file : files) {

                final Path path = Paths.get(file);

                FileFinder.find(path, "*.java", p -> {
                    try (FileInputStream in = new FileInputStream(p.toFile())) {
                        CompilationUnit cu = JavaParser.parse(in);

                        final List<JavaClassDeclaration> types = cu.getTypes().stream().map(t -> new JavaClassDeclaration(t.getName())).collect(Collectors.toList());
                        sources.add(new JavaSourceFile(path.relativize(p), cu.getPackage().getChildrenNodes().get(0).toString(), types));

                    } catch (IOException | com.github.javaparser.ParseException e) {
                        System.out.println("Failed ot parse file " + p);
                    }
                });
            }

            HashMap<String, Object> metric = new HashMap<>();

            metric.put("timestamp", Instant.now().getLong(ChronoField.INSTANT_SECONDS));
            metric.put("collector", "kuona.java.analyser");
            metric.put("paths", files);
            metric.put("sources", sources);
            metric.put("dataType", "source-code");

            final PostResults postResults = new PostResults(resultUrl);

            Gson g = new Gson();
            postResults.post(g.toJson(metric).getBytes());

        } catch (RuntimeException re) {
            System.err.println(re.toString());
            printHelp(System.out);
            return;
        }

    }

    protected static void printHelp(OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(writer, 80, "java -jar kuona-maven-analyser.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }

    static CommandLine parseOptions(String[] args) {
        try {
            Options options = commandLineOptions();


            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            return cmd;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Options commandLineOptions() {
        Options options = new Options();
        final Option resultOption = new Option("r", "result", true, "URL of the build server");
        resultOption.setRequired(true);
        options.addOption(resultOption);

        options.addOption(new Option("h", "help", false, "Output this message"));
        return options;
    }


}
