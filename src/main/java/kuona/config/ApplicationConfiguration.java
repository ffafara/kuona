package kuona.config;

import kuona.processor.JenkinsProcessor;

import java.util.List;

public interface ApplicationConfiguration {

    public String name();

    public List<JenkinsProcessor> servers();

    public String getSitePath();

    List<RepositorySpec> repositories();
}
