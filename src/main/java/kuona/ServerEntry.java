package kuona;

public class ServerEntry {
    private String name;
    private String description;
    private String uri;
    private int jobs;
    private int builds;

    public int getJobs() {
        return jobs;
    }

    public int getBuilds() {
        return builds;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }
}
