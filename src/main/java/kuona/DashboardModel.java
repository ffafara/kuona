package kuona;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardModel {
    protected String title;
    protected String version;
    private double averageBuildTime;
    private List<ServerEntry> servers;
    private Map<String, String> sparklines;
    private Date lastUpdated;

    public Map<String, String> getSparklines()

    {
        return sparklines;
    }

    public String getTitle() {
        return title;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public double getAverageBuildTime() {
        return averageBuildTime;
    }

    public String getVersion() {
        return version;
    }

    public List<ServerEntry> getServers() {
        return servers;
    }

    public int getServerCount() {
        return servers.size();
    }
}
