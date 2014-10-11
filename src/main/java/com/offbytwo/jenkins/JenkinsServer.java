/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package com.offbytwo.jenkins;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.MainView;
import kuona.JenkinsClient;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * The main starting point for interacting with a Jenkins server.
 */
public class JenkinsServer {
    private final JenkinsClient client;


    public URI getURI() {
       return client.getURI();
    }

    /**
     * Create a new Jenkins server directly from an HTTP client (ADVANCED)
     *
     * @param client Specialized client to use.
     */
    public JenkinsServer(JenkinsClient client) {
        this.client = client;
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
}
