package kuona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.Application;
import kuona.FileTemplate;
import kuona.config.KuonaSpec;
import kuona.metric.BuildTriggers;
import kuona.metric.ByDuration;
import kuona.model.*;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static kuona.FileTemplate.property;
import static kuona.utils.Utils.puts;

public class SiteUpdate {

    public static final String BUILDS_BY_RESULT_CHART_JS_FILENAME = "builds-by-result-chart.js";
    public static final String BUILDS_BY_DURATION_JS_FILENAME = "builds-by-duration.js";
    public static final String BUILDS_BY_TRIGGER_JS_FILENAME = "builds-by-trigger-chart.js";
    public static final String DASHBOARD_JSON_FILENAME = "dashboard.json";
    public static final String ACTIVITY_CHART_JS_FILENAME = "activity-chart.js";
    private final KuonaSpec config;

    public SiteUpdate(KuonaSpec config) {

        this.config = config;
    }

    public void update() {
        updateSiteBuildData();

        config.repositoryProcessors().stream().forEach(processor -> processor.process(config.getSite()));
    }

    private void updateSiteBuildData() {
        ArrayList<BuildWithDetails> completedBuilds = new ArrayList<>();
        try {
            puts("Updating CI data");
            String sitePath = config.getSitePath();

            Map<String, Object> dashboard = new HashMap<>();
            List<HashMap<String, Object>> dashboardServers = new ArrayList<>();

            Map<Integer, int[]> activity = new HashMap<>();

            Map<BuildResult, Integer> buildCountsByResult = new HashMap<>();
            for (BuildResult br : BuildResult.values()) {
                buildCountsByResult.put(br, 0);
            }
            ByDuration byDuration = new ByDuration();
            BuildTriggers triggers = new BuildTriggers();

            config.buildProcessors().stream().forEach(jenkins -> {
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
                                    actions.stream().filter(action -> action != null).forEach(action -> {
                                        if (action.containsKey("causes")) {
                                            List<HashMap> causes = (List<HashMap>) action.get("causes");

                                            causes.stream().filter(cause -> cause.containsKey("shortDescription")).forEach(cause -> {
                                                triggers.add((String) cause.get("shortDescription"));
                                            });
                                        }
                                    });
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

            FileTemplate.get(ACTIVITY_CHART_JS_FILENAME)
                    .with(property("buildcounts", dayData(activity)))
                    .renderTo(sitePath + File.separatorChar + ACTIVITY_CHART_JS_FILENAME);

            writeBuildsByResult(sitePath, buildCountsByResult);

            FileTemplate.get(BUILDS_BY_DURATION_JS_FILENAME)
                    .with(property("bands", byDuration.getBands()))
                    .renderTo(sitePath + File.separatorChar + BUILDS_BY_DURATION_JS_FILENAME);

            FileTemplate.get(BUILDS_BY_TRIGGER_JS_FILENAME)
                    .with(property("triggers", triggers.iterator()))
                    .renderTo(sitePath + File.separatorChar + BUILDS_BY_TRIGGER_JS_FILENAME);

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

    private void writeBuildsByResult(String sitePath, Map<BuildResult, Integer> buildCountsByResult) {
        int count = 0;
        for (int i : buildCountsByResult.values()) {
            count += i;
        }

        Map<BuildResult, Long> buildPercentagesByResult = new HashMap<>();
        final int c = count;
        buildCountsByResult.keySet().stream().forEach(result -> {
            final double percentage = (buildCountsByResult.get(result) * 100.0) / c;
            buildPercentagesByResult.put(result, (long) Math.floor(percentage + 0.4));
        });


        FileTemplate.get(BUILDS_BY_RESULT_CHART_JS_FILENAME)
                .with(property("outcomes", BuildResult.values()))
                .with(property("counts", buildPercentagesByResult))
                .renderTo(sitePath + File.separatorChar + BUILDS_BY_RESULT_CHART_JS_FILENAME);
    }
}
