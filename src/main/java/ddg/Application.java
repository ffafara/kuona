package ddg;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import ddg.generator.Site;
import ddg.generator.SiteGenerator;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STRawGroupDir;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Application {
    public void run(String[] args) {
        if (args.length < 1) {
            if (configExists()) {
                updateSite();
            } else {
                usage();
            }
        } else {
            List<String> arguments = Arrays.asList(args);

            arguments.remove(0);
            switch (args[0].toLowerCase()) {
                case "create":
                    createSite(arguments);
                    break;
            }
        }


    }

    private void updateSite() {

        try {
            Configuration config = Configuration.read(new FileInputStream("config.yml"));

            for (JenkinsServer jenkins : config.servers()) {

                Map<String, Job> jobs = jenkins.getJobs();

                for (String key : jobs.keySet()) {
                    final Job job = jobs.get(key);
                    System.out.println(job.details().getDisplayName());
                    final List<Build> builds = job.details().getBuilds();

                    for (Build build : builds) {
                        final BuildWithDetails details = build.details();

                        ST template = new ST("Build <build.Number> " +
                                "took: <build.Duration> " +
                                "Status: <build.Result>");

                        template.add("build", details);

                        puts(template.render());

//                    System.out.println("Build " + build.getNumber() + " took " + details.getDuration() + " result " + details.getResult().name());

                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean configExists() {
        return new File("config.yml").isFile();
    }

    private void createSite(List<String> arguments) {

        SiteGenerator generator = new SiteGenerator(new Site());

        generator.generate("site", arguments.get(0));
    }

    private void usage() {
        puts("Usage:" +
                "ddg <command> [command-args]\n" +
                "\n" +
                "ddg run in a project folder without any parameters updates the project data by reading from the configured CI systems." +
                "\n" +
                "Commands:\n" +
                "\n" +
                "create name    Create a new projects in the named directory. Once created you can update the config.yml\n" +
                "               file with the required CI settings.\n" +
                "\n");
    }

    public static void puts(String message) {
        System.out.println(message);
    }

    private static void testOne() {
        try {
            puts("***");
            STGroup g = new STRawGroupDir("templates/project/");

            puts("Reading a text file template file from the classpath");
            ST st = g.getInstanceOf("_config.yml");
            st.add("name", "Graham Brooks");

            System.out.println(st.render());
            puts("***");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testOneA() {
        try {
            puts("***");
            STGroup g = new STRawGroupDir("templates/project/");

            puts("Reading a text file template file from the classpath");
            ST st = g.getInstanceOf("_config.yml");

            System.out.println(st.render());
            puts("***");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testTwo() {
        try {
            puts("***");
            STGroup g = new STGroupDir("templates/project");
            puts("Reading a foobar template containing template syntax");
            ST st2 = g.getInstanceOf("foobar");
            System.out.println(st2.render());
            puts("***");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testThree() {
        try {
            puts("***");
            puts("Reading template file using a path");

            ST config = new ST("templates/project/_config.yml.st");

            System.out.println(config.render());
            puts("***");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
