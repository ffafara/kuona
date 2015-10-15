/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.jenkins.analyser.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobWithDetails extends Job implements Mergable<JobWithDetails>, Cloneable {
    String displayName;
    boolean buildable;
    List<Build> builds;
    Build lastBuild;
    Build lastCompletedBuild;
    Build lastFailedBuild;
    Build lastStableBuild;
    Build lastSuccessfulBuild;
    Build lastUnstableBuild;
    Build lastUnsuccessfulBuild;
    int nextBuildNumber;
    List<Job> downstreamProjects;
    List<Job> upstreamProjects;

    public JobWithDetails() {
        builds = new ArrayList<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public List<Build> getBuilds() {
        return Lists.transform(builds, this::buildWithClient);
    }

    private Build buildWithClient(Build from) {
        Build ret = new Build(from, getClient());
        return ret;
    }

    public Build getLastBuild() {
        return buildWithClient(lastBuild);
    }

    public Build getLastCompletedBuild() {
        return buildWithClient(lastCompletedBuild);
    }

    public Build getLastFailedBuild() {
        return buildWithClient(lastFailedBuild);
    }

    public Build getLastStableBuild() {
        return buildWithClient(lastStableBuild);
    }

    public Build getLastSuccessfulBuild() {
        return buildWithClient(lastSuccessfulBuild);
    }

    public Build getLastUnstableBuild() {
        return buildWithClient(lastUnstableBuild);
    }

    public Build getLastUnsuccessfulBuild() {
        return buildWithClient(lastUnsuccessfulBuild);
    }

    public int getNextBuildNumber() {
        return nextBuildNumber;
    }

    public List<Job> getDownstreamProjects() {
        return Lists.transform(downstreamProjects, new JobWithClient());
    }

    public List<Job> getUpstreamProjects() {
        return Lists.transform(upstreamProjects, new JobWithClient());
    }

    @Override
    public <T> T merge(T other) {

        try {
            final JobWithDetails merged = (JobWithDetails) this.clone();

            final Stream<Build> buildStream = ((JobWithDetails) other).builds.stream().filter(p -> !merged.builds.contains(p));
            merged.builds.addAll(buildStream.collect(Collectors.toList()));
            return (T) merged;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private class JobWithClient implements Function<Job, Job> {
        @Override
        public Job apply(Job job) {
            job.setClient(getClient());
            return job;
        }
    }
}
