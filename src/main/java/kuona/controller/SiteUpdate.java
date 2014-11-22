package kuona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.Application;
import kuona.FileTemplate;
import kuona.config.KuonaSpec;
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
        try {
            puts("Updating CI data");
            String sitePath = config.getSitePath();
            final BuildMetrics metrics= new BuildMetrics();

            config.buildProcessors().stream().forEach(jenkins -> {
                jenkins.collectMetrics(metrics);
            });

            metrics.dashboard.put("title", "Kuona build analytics");
            metrics.dashboard.put("servers", metrics.dashboardServers);
            metrics.dashboard.put("lastUpdated", new Date());
            metrics.dashboard.put("version", Application.VERSION);
            metrics.dashboard.put("averageBuildTime", metrics.completedBuilds.stream().filter(b -> !b.isBuilding()).collect(Collectors.averagingInt(b -> b.getDuration())));


            generateSparklines(metrics.dashboard, metrics.activity);

            FileTemplate.get(ACTIVITY_CHART_JS_FILENAME)
                    .with(property("buildcounts", dayData(metrics.activity)))
                    .renderTo(sitePath + File.separatorChar + ACTIVITY_CHART_JS_FILENAME);

            writeBuildsByResult(sitePath, metrics.buildCountsByResult);

            FileTemplate.get(BUILDS_BY_DURATION_JS_FILENAME)
                    .with(property("bands", metrics.byDuration.getBands()))
                    .renderTo(sitePath + File.separatorChar + BUILDS_BY_DURATION_JS_FILENAME);

            FileTemplate.get(BUILDS_BY_TRIGGER_JS_FILENAME)
                    .with(property("triggers", metrics.triggers.iterator()))
                    .renderTo(sitePath + File.separatorChar + BUILDS_BY_TRIGGER_JS_FILENAME);

            writeDashboardDataFile(sitePath, metrics.dashboard);

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
