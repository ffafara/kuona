package kuona.gocd.analyser;

import org.apache.commons.cli.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        try {
            CommandLine options = parseOptions(args);
            CollectorConfig collectorConfig = new CollectorConfig(
                    options.getOptionValue('u'),
                    options.getOptionValue('n')
            );

//            MetricConfig config = new MetricConfig(collectorConfig);
            collectorConfig.fetch();

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("collectorConfig", collectorConfig);
//            jobDataMap.put("metricConfig", config);

            JobDetail job = JobBuilder.newJob(Collector.class)
                    .withIdentity("GoCdCollector", "collectors")
                    .usingJobData(jobDataMap)
                    .build();

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("GoCdTrigger", "collectors")
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(collectorConfig.getFrequency()).repeatForever())
                    .build();
            try {
                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
                scheduler.scheduleJob(job, trigger);

            } catch (SchedulerException se) {
                se.printStackTrace();
            }
        } catch (RuntimeException re) {
            System.err.println(re.toString());
            printHelp(System.out);
            return;
        }
    }

    protected static CommandLine parseOptions(String[] args) {
        try {
            Options options = commandLineOptions();

            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Options commandLineOptions() {
        Options options = new Options();

        Option configUrl = new Option("u", "url", true, "URL for collector to fetch configuration from Kuona Web App");
        configUrl.setRequired(true);
        options.addOption(configUrl);
        Option metricName = new Option("n", "name", true, "Metric name");
        metricName.setRequired(true);
        options.addOption(metricName);

        return options;
    }

    protected static void printHelp(OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(writer, 80, "java -jar gocd-analyser.jar [options] <paths>", "\nOptions", commandLineOptions(), 3, 2, "\n See http://kuona.io");
        writer.close();
    }
}
