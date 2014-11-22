package kuona.server;

import kuona.controller.BuildMetrics;

public interface BuildProcessor {
    public void collectMetrics(BuildMetrics metrics);
}
