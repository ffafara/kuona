package kuona.snapci.analyser.model;

import java.util.List;

public class Pipeline {
    private int counter;
    private String result;
    private List<Stage> stages;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getResult() {
        return result;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
