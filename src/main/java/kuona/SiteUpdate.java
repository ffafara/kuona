package kuona;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.MainView;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
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

            Map<Integer, int[]> activity = new HashMap<>();

            config.servers().stream().forEach(jenkins -> {
                try {
                    puts("Updating " + jenkins.getURI());
                    final int[] jobCount = {0};
                    final int[] buildCount = {0};
                    Set<String> jobNames = jenkins.getJobs().keySet();
                    jobCount[0] = jobNames.size();
                    jobNames.stream().forEach(key -> {
                        try {
                            JobWithDetails job = jenkins.getJob(key);
                            puts("Updating " + key);
                            final List<Build> builds = job.details().getBuilds();

                            buildCount[0] += builds.size();

                            builds.stream().forEach(buildDetails -> {
                                try {
                                    final BuildWithDetails details = buildDetails.details();
                                    Timestamp timestamp = new Timestamp(details.getTimestamp());

                                    Date buildDate = new Date(timestamp.getTime());

                                    int year = buildDate.getYear() + 1900;

                                    if (!activity.containsKey(year)) {
                                        activity.put(year, new int[12]);
                                    }

                                    int[] yearMap = activity.get(year);
                                    yearMap[buildDate.getMonth()] += 1;

//                                    puts(job.getDisplayName() + " - " + buildDate);
                                    completedBuilds.add(details);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    dashboardServers.add(new HashMap<String, Object>() {
                        {
                            MainView serverInfo = jenkins.getServerInfo();
                            put("name", serverInfo.getName());
                            put("description", serverInfo.getDescription());
                            put("uri", jenkins.getURI().toString());
                            put("jobs", jobCount[0]);
                            put("builds", buildCount[0]);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            dashboard.put("title", "Kuona build analytics");
            dashboard.put("servers", dashboardServers);
            dashboard.put("lastUpdated", new Date());
            dashboard.put("version", Application.VERSION);
            dashboard.put("averageBuildTime", completedBuilds.stream().filter(b -> !b.isBuilding()).collect(Collectors.averagingInt(b -> b.getDuration())));


            generateSparklines(dashboard, activity);

            ArrayList dayData = dayData(activity);

            writeActivityChartFile(sitePath, dayData);

            Map trend = new HashMap<String, Object>() {{
                put("trend", "up");
                put("delta", 10);
            }};
            dashboard.put("trend", trend);
            ObjectMapper mapper = new ObjectMapper();

            String dashboardFilepath = sitePath + File.separatorChar + "dashboard.json";
            puts("Updating dashboard data " + dashboardFilepath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(dashboardFilepath), dashboard);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSparklines(Map<String, Object> dashboard, final Map<Integer, int[]> activity) {
        Date today = new Date();
        Map sparklines = new HashMap<String, Object>() {{
            int currentYear = today.getYear() + 1900;
            if (activity.containsKey(currentYear)) {
                int[] values = activity.get(currentYear);
                put("activity", values[0] +
                        ", " + values[1] +
                        ", " + values[2] +
                        ", " + values[3] +
                        ", " + values[4] +
                        ", " + values[5] +
                        ", " + values[6] +
                        ", " + values[7] +
                        ", " + values[8] +
                        ", " + values[9] +
                        ", " + values[10] +
                        ", " + values[11]);
            }
        }};


        dashboard.put("sparklines", sparklines);
    }

    private ArrayList dayData(final Map<Integer, int[]> activity) {
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return new ArrayList() {
            {
                activity.keySet().stream().forEach(year -> {
                    for (int i = 0; i < months.length; i++) {
                        final int index = i;
                        add(new HashMap<String, Object>() {{
                            put("elapsed", months[index] + " " + year);
                            put("value", activity.get(year)[index]);
                        }});
                    }
                });
            }
        };
    }

    private void writeActivityChartFile(String sitePath, ArrayList dayData) throws IOException {
        STGroup g = new STRawGroupDir("templates/project/");

        puts("Reading a text file template file from the classpath");
        ST st = g.getInstanceOf("activity-chart.js");
        st.add("buildcounts", dayData);

        String activityChartFilepath = sitePath + File.separatorChar + "activity-chart.js";
        puts("Updating activitiy chart data " + activityChartFilepath);

        FileWriter activityChartFile = new FileWriter(activityChartFilepath);
        activityChartFile.write(st.render());
        activityChartFile.close();
    }
}
