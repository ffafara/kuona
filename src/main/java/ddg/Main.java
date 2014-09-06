package ddg;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STRawGroupDir;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void puts(String message) {
        System.out.println(message);
    }

    public static void main(String args[]) throws URISyntaxException, IOException {
        if (args.length < 3) {
            System.out.println("Usage ddg [jenkins:port] <username> <password/key>");
        } else {
//            STGroup.verbose = true;
            testOne();
//            testOneA();
//            testTwo();
//
//            testThree();
//

            JenkinsServer jenkins = new JenkinsServer(new URI(args[0]), args[1], args[2]);

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
