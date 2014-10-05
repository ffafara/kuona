package kuona;

import java.util.Date;
import java.util.List;

public class DashboardModel {
    protected String version;
    private double averageBuildTime;
    private List<ServerEntry> servers;
    private Date lastUpdated;

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
}
