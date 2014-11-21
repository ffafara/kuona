package kuona.config;

import kuona.server.JenkinsServer;

import java.util.List;

public interface ApplicationConfiguration {

    public String name();

    public List<JenkinsServer> servers();

    public String getSitePath();

    List<RepositorySpec> repositories();
}
