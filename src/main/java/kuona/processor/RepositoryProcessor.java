package kuona.processor;

import kuona.config.SiteSpec;

public interface RepositoryProcessor {
    String getName();

    void process(SiteSpec site);
}
