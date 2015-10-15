package kuona.jenkins.analyser;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import kuona.jenkins.analyser.model.*;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static kuona.jenkins.analyser.utils.Utils.puts;

public class JenkinsServer {
    private final JenkinsClient client;

    public JenkinsServer(String url, String username, String password) {
        try {
            final URI uri = new URI(url);
            final Project project = new Project(uri);
            this.client = new JenkinsClient(project, uri, username, password);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public JenkinsServer(JenkinsClient client) {
        this.client = client;
    }

    public URI getURI() {
        return client.getURI();
    }

    /**
     * Get the current status of the Jenkins end-point by pinging it.
     *
     * @return true if Jenkins is up and running, false otherwise
     */
    public boolean isRunning() {
        try {
            client.get("/");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public MainView getServerInfo() {
        try {
            return client.get("/", MainView.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a list of all the defined jobs on the server (at the summary level)
     *
     * @return list of defined jobs (summary level, for details @see Job#details
     * @throws IOException
     */
    public Map<String, Job> getJobs() throws IOException {
        List<Job> jobs = client.get("/", MainView.class).getJobs();
        return Maps.uniqueIndex(jobs, job -> {
            job.setClient(client);
            return job.getName().toLowerCase();
        });
    }

    /**
     * Get a single Job from the server.
     *
     * @return A single Job, null if not present
     * @throws IOException
     */
    public JobWithDetails getJobDetails(String jobName) throws IOException {
        try {
            JobWithDetails job = client.get("/job/" + encode(jobName), JobWithDetails.class);
            job.setClient(client);

            return job;
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == 404) {
                return null;
            }
            throw e;
        }

    }


    /**
     * Get a list of all the computers on the server (at the summary level)
     *
     * @return list of defined computers (summary level, for details @see Computer#details
     * @throws IOException
     */
    public Map<String, Computer> getComputers() throws IOException {
        List<Computer> computers = client.get("computer/", Computer.class).getComputers();
        return Maps.uniqueIndex(computers, new Function<Computer, String>() {
            @Override
            public String apply(Computer computer) {
                computer.setClient(client);
                return computer.getDisplayName().toLowerCase();
            }
        });
    }

    private String encode(String pathPart) {
        // jenkins doesn't like the + for space, use %20 instead
        return URLEncoder.encode(pathPart).replaceAll("\\+", "%20");
    }

    public void collectMetrics(BuildMetrics metrics) {
        try {
            puts("Updating " + getURI());

            Set<String> jobNames = getJobs().keySet();

            jobNames.stream().forEach(key -> {
                try {
                    JobWithDetails job = getJobDetails(key);
                    puts("Updating " + key);

                    final List<Build> builds = job.details().getBuilds();

                    builds.stream().forEach(buildDetails -> {
                        try {
                            final BuildWithDetails details = buildDetails.details();

                            final String value = buildDetails.detailsJSon();

                            String entry = "{\n" +
                                    "  \"job\": " + job.detailsJson() + " ,\n" +
                                    "  \"build\": " + value + "\n" +
                                    "}";

                            System.out.println(entry);
//                            metrics.prepareIndex("kuona", "build", details.getId())
//                                    .setSource(entry)
//                                    .execute();
//                                    .actionGet();

                        } catch (Throwable e1) {
                            e1.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return "Jenkins";
    }
}
