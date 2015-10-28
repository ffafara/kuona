package kuona.snapci.analyser;

import org.apache.commons.cli.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static kuona.snapci.analyser.utils.Utils.puts;

public class Main {

    public static void main(String[] args) {

        puts("Kuona snap-ci analyser");

        CommandLine options = parseOptions(args);
        KuonaAppConfig kuonaAppConfig = new KuonaAppConfig(
                options.getOptionValue('u'),
                options.getOptionValue('n')
        );

        MetricConfig config = new MetricConfig(kuonaAppConfig);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("kuonaAppConfig", kuonaAppConfig);
        jobDataMap.put("metricConfig", config);

        JobDetail job = JobBuilder.newJob(Collector.class)
                .withIdentity("SnapCiCollector", "collectors")
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("SnapCiTrigger", "collectors")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(30).repeatForever())
                .build();
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static CommandLine parseOptions(String[] args) {
        try {
            Options options = commandLineOptions();

            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Options commandLineOptions() {
        Options options = new Options();

        Option kuonaURL = new Option("u", "url", true, "URL in Kuona Web App to use for this collector");
        kuonaURL.setRequired(true);
        options.addOption(kuonaURL);
        Option snapUser = new Option("n", "name", true, "Metric name");
        snapUser.setRequired(true);
        options.addOption(snapUser);

        return options;
    }

}

