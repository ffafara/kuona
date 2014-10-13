package kuona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.Application;
import kuona.config.ApplicationConfiguration;
import kuona.metric.BuildTriggers;
import kuona.metric.ByDuration;
import kuona.model.*;
import org.joda.time.DateTime;
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

    public static final String BUILDS_BY_RESULT_CHART_JS_FILENAME = "builds-by-result-chart.js";
    public static final String BUILDS_BY_DURATION_JS_FILENAME = "builds-by-duration.js";
    public static final String BUILDS_BY_TRIGGER_JS_FILENAME = "builds-by-trigger-chart.js";
    public static final String DASHBOARD_JSON_FILENAME = "dashboard.json";
    public static final String ACTIVITY_CHART_JS_FILENAME = "activity-chart.js";
    public static final String TEMPLATES_PROJECT_PATH = "templates/project/";
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

            Map<BuildResult, Integer> buildCountsByResult = new HashMap<>();
            for (BuildResult br : BuildResult.values()) {
                buildCountsByResult.put(br, 0);
            }
            ByDuration byDuration = new ByDuration();
            BuildTriggers triggers = new BuildTriggers();

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

                                    if (details.getResult() == null) {
                                        buildCountsByResult.put(BuildResult.UNKNOWN, buildCountsByResult.get(BuildResult.UNKNOWN) + 1);
                                    } else {
                                        buildCountsByResult.put(details.getResult(), buildCountsByResult.get(details.getResult()) + 1);
                                    }

                                    byDuration.collect(details.getDuration());
                                    final List<Map> actions = details.getActions();
                                    for (Map action : actions) {
                                        if (action != null) {
                                            if (action.containsKey("causes")) {
                                                List<HashMap> causes = (List<HashMap>) action.get("causes");

                                                for (HashMap cause : causes) {

                                                    if (cause.containsKey("shortDescription")) {
                                                        triggers.add((String) cause.get("shortDescription"));
                                                    }
                                                }
                                            }
                                        }
                                    }
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

            writeActivityChartFile(sitePath, dayData(activity));
            writeBuildsByResult(sitePath, buildCountsByResult);
            writeBuildsByDuration(sitePath, byDuration);
            writeBuildsByTrigger(sitePath, triggers);

            writeDashboardDataFile(sitePath, dashboard);

            puts("\nUpdate Complete");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDashboardDataFile(String sitePath, Map<String, Object> dashboard) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String dashboardFilepath = sitePath + File.separatorChar + DASHBOARD_JSON_FILENAME;
        puts("Updating dashboard data " + dashboardFilepath);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(dashboardFilepath), dashboard);
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

        DateTime startDate = new DateTime().minusYears(1);

        return new ArrayList() {
            {
                activity.keySet().stream().forEach(year -> {
                    for (int i = 0; i < months.length; i++) {
                        final int index = i;
                        DateTime eventDate = new DateTime(year, index + 1, 1, 0, 0);

                        if (eventDate.isAfter(startDate) && eventDate.isBeforeNow()) {
                            add(new HashMap<String, Object>() {{
                                put("elapsed", months[index] + " " + year);
                                put("value", activity.get(year)[index]);
                            }});
                        }
                    }
                });
            }
        };
    }

    private void writeActivityChartFile(String sitePath, ArrayList dayData) throws IOException {
        puts("Generating activity chart file");
        STGroup g = new STRawGroupDir(TEMPLATES_PROJECT_PATH);

        ST st = g.getInstanceOf(ACTIVITY_CHART_JS_FILENAME);
        if (st == null) {
            puts("Failed to load " + ACTIVITY_CHART_JS_FILENAME + " template. Site not updated correctly");
            return;
        }

        st.add("buildcounts", dayData);

        String activityChartFilepath = sitePath + File.separatorChar + ACTIVITY_CHART_JS_FILENAME;
        puts("Updating activitiy chart data " + activityChartFilepath);

        FileWriter activityChartFile = new FileWriter(activityChartFilepath);
        activityChartFile.write(st.render());
        activityChartFile.close();
    }

    private void writeBuildsByDuration(String sitePath, ByDuration byDuration) throws IOException {
        STGroup g = new STRawGroupDir(TEMPLATES_PROJECT_PATH);

        ST st = g.getInstanceOf(BUILDS_BY_DURATION_JS_FILENAME);
        if (st == null) {
            puts("Failed to load " + BUILDS_BY_DURATION_JS_FILENAME + " template. Site not updated correctly");
            return;
        }
        st.add("bands", byDuration.getBands());

        String activityChartFilepath = sitePath + File.separatorChar + BUILDS_BY_DURATION_JS_FILENAME;
        puts("Updating activitiy chart data " + activityChartFilepath);

        FileWriter activityChartFile = new FileWriter(activityChartFilepath);
        activityChartFile.write(st.render());
        activityChartFile.close();
    }

    private void writeBuildsByTrigger(String sitePath, BuildTriggers triggers) throws IOException {
        STGroup g = new STRawGroupDir(TEMPLATES_PROJECT_PATH);

        ST st = g.getInstanceOf(BUILDS_BY_TRIGGER_JS_FILENAME);
        if (st == null) {
            puts("Failed to load " + BUILDS_BY_TRIGGER_JS_FILENAME + " template. Site not updated correctly");
            return;
        }
        st.add("triggers", triggers.iterator());

        String activityChartFilepath = sitePath + File.separatorChar + BUILDS_BY_TRIGGER_JS_FILENAME;
        puts("Updating chart data " + activityChartFilepath);

        FileWriter activityChartFile = new FileWriter(activityChartFilepath);
        activityChartFile.write(st.render());
        activityChartFile.close();
    }

    private void writeBuildsByResult(String sitePath, Map<BuildResult, Integer> buildCountsByResult) {
        try {
            int count = 0;
            for (int i : buildCountsByResult.values()) {
                count += i;
            }

            Map<BuildResult, Long> buildPercentagesByResult = new HashMap<>();
            final int c = count;
            buildCountsByResult.keySet().stream().forEach(result -> {
                final double percentage = (buildCountsByResult.get(result) * 100.0) / c;
                buildPercentagesByResult.put(result, (long)Math.floor(percentage + 0.4));
            });


            STGroup g = new STRawGroupDir(TEMPLATES_PROJECT_PATH);
            ST st = g.getInstanceOf(BUILDS_BY_RESULT_CHART_JS_FILENAME);
            if (st == null) {
                puts("Failed to load " + BUILDS_BY_RESULT_CHART_JS_FILENAME + " template. Site not updated correctly");
                return;
            }

            st.add("outcomes", BuildResult.values());
            st.add("counts", buildPercentagesByResult);
            String buildsByResultFilepath = sitePath + File.separatorChar + BUILDS_BY_RESULT_CHART_JS_FILENAME;
            puts("Updating build results chart data " + buildsByResultFilepath);

            FileWriter activityChartFile = new FileWriter(buildsByResultFilepath);
            activityChartFile.write(st.render());
            activityChartFile.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write builds by result", e);
        }
    }
}
