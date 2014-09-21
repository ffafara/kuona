package kuona;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import kuona.generator.Site;
import kuona.generator.SiteGenerator;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STRawGroupDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
                default:
                    System.err.println("Unrecognised command " + args[0] + "\n");
                    usage();
                    break;
            }
        }
    }

    private void startServer(List<String> arguments) {
        Configuration config = null;
        try {
            config = Configuration.read(new FileInputStream("config.yml"));

        String sitePath = config.getSitePath();

        KuonaServer server = new KuonaServer(sitePath);

        server.start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, Object> jsonToMap(String data) {
        try {
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());

            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {
            };

            return mapper.readValue(data, typeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void writeJson(Object o, Writer output) {
        try {
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(output, o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prettifyJson(String data) {
        puts("\n\n********************************************************************************************************************************************\n");
        HashMap<String, Object> o = jsonToMap(data);
        StringWriter sw = new StringWriter();
        writeJson(o, sw);
        puts(sw.toString());
    }

    private void updateSite() {
        SiteUpdate update = new SiteUpdate();

        update.update();
    }

    private boolean configExists() {
        return new File("config.yml").isFile();
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
