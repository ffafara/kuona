/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package com.offbytwo.jenkins.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainView extends BaseModel {
    private List<Job> jobs;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    /* default constructor needed for Jackson */
    public MainView() {
        this(new ArrayList<Job>());
    }

    public MainView(List<Job> jobs) {
        this.jobs = jobs;
    }

    public MainView(Job... jobs) {
        this(Arrays.asList(jobs));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
