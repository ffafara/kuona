package com.delivery.dashboard.generator;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String args[]) throws URISyntaxException, IOException {
        if (args.length < 3) {
            System.out.println("Usage ddg [jenkins:port] <username> <password/key>");
        } else {
            JenkinsServer jenkins = new JenkinsServer(new URI(args[0]), args[1], args[2]);


            Map<String, Job> jobs = jenkins.getJobs();

            for (String key : jobs.keySet()) {
                final Job job = jobs.get(key);
                System.out.println(job.details().getDisplayName());
                final List<Build> builds = job.details().getBuilds();

                for (Build build : builds) {
                    final BuildWithDetails details = build.details();
                    System.out.println("Build " + build.getNumber() + " took " + details.getDuration() + " result " + details.getResult().name());

                }
            }
        }
    }
}
