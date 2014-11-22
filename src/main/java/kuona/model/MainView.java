/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainView extends BaseModel implements Mergable<MainView>, Cloneable {
    private List<Job> jobs;
    private String description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public <T> T merge(T other) {
        try {
            MainView merged = (MainView) this.clone();

            final Stream<Job> buildStream = ((MainView) other).jobs.stream().filter(p -> !merged.jobs.contains(p));
            merged.jobs.addAll(buildStream.collect(Collectors.toList()));
            return (T) merged;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
