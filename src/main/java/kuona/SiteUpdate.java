package kuona;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static kuona.utils.Utils.puts;

public class SiteUpdate {

    private final ApplicationConfiguration config;

    public SiteUpdate(ApplicationConfiguration config) {

        this.config = config;
    }

    public void update() {
        ArrayList<BuildWithDetails> completedBuilds = new ArrayList<>();
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


            config.servers().stream().forEach(jenkins -> {
                try {
                    jenkins.getJobs().keySet().stream().forEach(key -> {
                        try {
                            final List<Build> builds = jenkins.getJob(key).details().getBuilds();

                            builds.stream().forEach(buildDetails -> {
                                try {
                                    final BuildWithDetails details = buildDetails.details();
                                    completedBuilds.add(details);
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

            dashboard.put("servers", dashboardServers);
            dashboard.put("lastUpdated", new Date());
            dashboard.put("version", Application.VERSION);
            dashboard.put("averageBuildTime", completedBuilds.stream().filter(b -> !b.isBuilding()).collect(Collectors.averagingInt(b -> b.getDuration())));


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


        } catch (Exception e) {
            throw new RuntimeException(e);
        }



//        Map<long, int> BuildByDate = roster
//                .stream()
//                .collect(
//                        Collectors.groupingBy(
//                                Person::getGender,
//                                Collectors.averagingInt(Person::getAge)));

    }
}
