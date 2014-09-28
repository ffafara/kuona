package kuona;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.JenkinsServer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kuona.utils.Utils.puts;

public class SiteUpdate {

    private final ApplicationConfiguration config;

    public SiteUpdate(ApplicationConfiguration config) {

        this.config = config;
    }

    public void update() {
        try {
            puts("Updating CI data");
            String sitePath = config.getSitePath();

            List<JenkinsServer> servers = config.servers();

            Map<String, Object> dashboard = new HashMap<>();
            List<Map> dashboardServers = new ArrayList<>();

            for (JenkinsServer jenkins : servers) {
                puts("Updating " + jenkins.getURI());
                dashboardServers.add(new HashMap<String, String>() {
                    {
                        put("name", jenkins.getServerInfo().getName());
                        put("description", jenkins.getServerInfo().getDescription());
                        put("uri", jenkins.getURI().toString());
                    }
                });
            }

            dashboard.put("servers", dashboardServers);
            dashboard.put("lastUpdated", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            dashboard.put("version", Application.VERSION);

            Map sparklines = new HashMap<String, Object>() {{
                put("activity", "110,150,300,130,400,240,220,310,220,300, 270, 210");
            }};


            dashboard.put("sparklines", sparklines);
            Map trend = new HashMap<String, Object>() {{
                put("trend", "up");
                put("delta", 10);
            }};
            dashboard.put("trend", trend);
            ObjectMapper mapper = new ObjectMapper();

            String dashboardFilepath = sitePath + File.separatorChar + "dashboard.json";
            puts("Updating dashboard data " + dashboardFilepath);
            mapper.writeValue(new File(dashboardFilepath), dashboard);


//            for (JenkinsServer jenkins : servers) {
//                Map<String, Job> jobs = jenkins.getJobs();
//
//                for (String key : jobs.keySet()) {
//                    final Job job = jobs.get(key);
//
//                    final List<Build> builds = job.details().getBuilds();
//
//                    for (Build buildDetails : builds) {
//                        prettifyJson(buildDetails.detailsJson());
//                    }
//                }
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
