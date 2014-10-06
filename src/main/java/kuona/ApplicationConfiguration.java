package kuona;

import com.offbytwo.jenkins.JenkinsServer;

import java.util.List;

public interface ApplicationConfiguration {

    public String name();

    public List<JenkinsServer> servers();

    public String getSitePath();
}
