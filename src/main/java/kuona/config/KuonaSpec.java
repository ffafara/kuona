package kuona.config;

import com.google.common.collect.Lists;
import kuona.processor.BuildProcessor;
import kuona.processor.ProcessorBuilder;
import kuona.processor.RepositoryProcessor;

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
        return Lists.transform(servers, this::buildServerProcessors);
    }

    public List<RepositoryProcessor> repositoryProcessors() {
        return Lists.transform(repositories, this::buildRepositoryProcessor);
    }

    private BuildProcessor buildServerProcessors(BuildServerSpec spec) {
        final ProcessorBuilder processorBulder = new ProcessorBuilder();
        BuildProcessor processor = processorBulder.build(spec, spec.getProcessor(), BuildProcessor.class);
        return processor;
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
