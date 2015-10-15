package kuona.jenkins.analyser.metric;

public class BuildTrigger {
    private int count;
    private String cause;

    public BuildTrigger(String cause) {
        this.cause = cause;
        this.count = 0;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        this.count++;
    }

    public String getCause() {
        return cause;
    }
}
