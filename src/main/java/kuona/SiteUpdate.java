package kuona;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;

import java.io.File;
import java.io.IOException;
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

            Map<String, Object> dashboard = new HashMap<>();
            List<Map> dashboardServers = new ArrayList<>();

            config.servers().stream().forEach(jenkins -> {
                puts("Updating " + jenkins.getURI());
                dashboardServers.add(new HashMap<String, String>() {
                    {
                        put("name", jenkins.getServerInfo().getName());
                        put("description", jenkins.getServerInfo().getDescription());
                        put("uri", jenkins.getURI().toString());
                    }
                });
            });

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


            config.servers().stream().forEach(jenkins -> {
                try {
                    jenkins.getJobs().keySet().stream().forEach(key -> {
                        try {
                            final List<Build> builds = jenkins.getJob(key).details().getBuilds();

                            builds.stream().forEach(buildDetails -> {
                                try {

                                    puts(buildDetails.detailsJson());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
