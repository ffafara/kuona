package kuona.gocd.analyser.model;

import java.util.List;

public class PipelineHistory {
    List<Pipeline> pipelines;
    Pagination pagination;

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }
}