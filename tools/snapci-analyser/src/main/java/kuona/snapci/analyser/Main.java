package kuona.snapci.analyser;

import org.apache.commons.cli.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static kuona.snapci.analyser.utils.Utils.puts;

public class Main {

    public static void main(String[] args) {

        puts("Kuona snap-ci analyser");

        CommandLine options = parseOptions(args);
        ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig(
                options.getOptionValue('n', "elasticsearch"),
                options.getOptionValue('h', "127.0.0.1"),
                Integer.parseInt(options.getOptionValue('p', "9200"))
        );

        SnapConfig snapConfig = new SnapConfig(
                options.getOptionValue("url"),
                options.getOptionValue("user"),
                options.getOptionValue("pass")
        );

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("elasticSearchConfig", elasticSearchConfig);
        jobDataMap.put("snapConfig", snapConfig);

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

        options.addOption(new Option("n", "clustername", true, "ElasticSearch cluster name"));
        options.addOption(new Option("h", "host", true, "ElasticSearch host"));
        options.addOption(new Option("p", "port", true, "ElasticSearch port"));
        Option snapURL = new Option("url", true, "Snap-Ci URL");
        snapURL.setRequired(true);
        options.addOption(snapURL);
        Option snapUser = new Option("user", true, "Snap-Ci user");
        snapUser.setRequired(true);
        options.addOption(snapUser);
        Option snapPass = new Option("pass", true, "Snap-Ci password (key)");
        snapPass.setRequired(true);
        options.addOption(snapPass);
        return options;
    }
}

