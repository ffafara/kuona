/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.processor;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import kuona.client.JenkinsClient;
import kuona.client.JenkinsHttpClient;
import kuona.client.JenkinsLocalClient;
import kuona.config.BuildServerSpec;
import kuona.controller.BuildMetrics;
import kuona.model.*;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import static kuona.utils.Utils.puts;

/**
 * The main starting point for interacting with a Jenkins server.
 */
public class JenkinsProcessor implements BuildProcessor {
    private final JenkinsClient client;


    public JenkinsProcessor(BuildServerSpec spec) {
        try {
            final URI uri = new URI(spec.getUrl());
            final Project project = new Project(uri);
            this.client = new JenkinsLocalClient(project, new JenkinsHttpClient(project, uri, spec.getUsername(), spec.getPassword()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public JenkinsProcessor(JenkinsClient client) {
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
            client.get("/", "");
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
    public JobWithDetails getJob(String jobName) throws IOException {
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
            final int[] jobCount = {0};
            final int[] buildCount = {0};
            Set<String> jobNames = getJobs().keySet();
            jobCount[0] = jobNames.size();
            jobNames.stream().forEach(key -> {
                try {
                    JobWithDetails job = getJob(key);
                    puts("Updating " + key);
                    final List<Build> builds = job.details().getBuilds();

                    buildCount[0] += builds.size();

                    builds.stream().forEach(buildDetails -> {
                        try {
                            final BuildWithDetails details = buildDetails.details();
                            Timestamp timestamp = new Timestamp(details.getTimestamp());

                            Date buildDate = new Date(timestamp.getTime());

                            int year = buildDate.getYear() + 1900;

                            if (!metrics.activity.containsKey(year)) {
                                metrics.activity.put(year, new int[12]);
                            }

                            int[] yearMap = metrics.activity.get(year);
                            yearMap[buildDate.getMonth()] += 1;

                            if (details.getResult() == null) {
                                metrics.buildCountsByResult.put(BuildResult.UNKNOWN, metrics.buildCountsByResult.get(BuildResult.UNKNOWN) + 1);
                            } else {
                                metrics.buildCountsByResult.put(details.getResult(), metrics.buildCountsByResult.get(details.getResult()) + 1);
                            }

                            metrics.byDuration.collect(details.getDuration());
                            final List<Map> actions = details.getActions();
                            actions.stream().filter(action -> action != null).forEach(action -> {
                                if (action.containsKey("causes")) {
                                    List<HashMap> causes = (List<HashMap>) action.get("causes");

                                    causes.stream().filter(cause -> cause.containsKey("shortDescription")).forEach(cause -> {
                                        metrics.triggers.add((String) cause.get("shortDescription"));
                                    });
                                }
                            });
                            metrics.completedBuilds.add(details);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            metrics.dashboardServers.add(new HashMap<String, Object>() {
                {
                    MainView serverInfo = getServerInfo();
                    put("name", serverInfo.getName());
                    put("description", serverInfo.getDescription());
                    put("uri", getURI().toString());
                    put("jobs", jobCount[0]);
                    put("builds", buildCount[0]);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "Jenkins";
    }
}
