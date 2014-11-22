package kuona.config;

import com.google.common.collect.Lists;
import kuona.client.JenkinsHttpClient;
import kuona.client.JenkinsLocalClient;
import kuona.model.Project;
import kuona.processor.ProcessorBuilder;
import kuona.processor.RepositoryProcessor;
import kuona.server.BuildProcessor;
import kuona.server.JenkinsServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class KuonaSpec {
    protected SiteSpec site;
    protected ArrayList<BuildServerSpec> servers;
    protected ArrayList<RepositorySpec> repositories;

    public KuonaSpec() {
        servers = new ArrayList<>();
        repositories = new ArrayList<>();
    }

    public SiteSpec getSite() {
        return site;
    }

    public List<BuildServerSpec> getBuildServers() {
        return servers;
    }

    public List<RepositorySpec> getRepositories() {
        return repositories;
    }

    public String getSitePath() {
        return site.getPath();
    }

    public List<BuildProcessor> buildProcessors() {
        return Lists.transform(servers, this::jenkinsServers);
    }

    public List<RepositoryProcessor> repositoryProcessors() {
        return Lists.transform(repositories, this::buildRepositoryProcessor);
    }

    private JenkinsServer jenkinsServers(BuildServerSpec spec) {
        try {

            final URI uri = new URI(spec.getUrl());
            final Project project = new Project(uri);
            return new JenkinsServer(new JenkinsLocalClient(project, new JenkinsHttpClient(project, uri, spec.getUsername(), spec.getPassword())));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private RepositoryProcessor buildRepositoryProcessor(RepositorySpec spec) {
        final ProcessorBuilder processorBulder = new ProcessorBuilder();
        RepositoryProcessor processor = processorBulder.build(spec, spec.getProcessor(), RepositoryProcessor.class);
        return processor;
    }

    public String getSiteName() {
        return site.getName();
    }
}
