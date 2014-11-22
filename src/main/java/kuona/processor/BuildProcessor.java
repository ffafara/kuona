package kuona.processor;

import kuona.controller.BuildMetrics;

public interface BuildProcessor {
    public void collectMetrics(BuildMetrics metrics);

    String getName();
}
