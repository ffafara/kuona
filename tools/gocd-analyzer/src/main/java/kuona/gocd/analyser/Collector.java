package kuona.gocd.analyser;

import kuona.gocd.analyser.metric.Metric;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

public class Collector implements Job {

    MetricConfig metricConfig;
    KuonaAppConfig kuonaAppConfig;

    public void setKuonaAppConfig(KuonaAppConfig kuonaAppConfig) {
        this.kuonaAppConfig = kuonaAppConfig;
    }

    public void setMetricConfig(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GoCdConfig goCdConfig = metricConfig.getGoCdConfig();
        HttpHost target = goCdConfig.getHost();
        Credentials defaultcreds = new UsernamePasswordCredentials(goCdConfig.getUser(), goCdConfig.getPassword());
        AuthScope authScope = new AuthScope(target.getHostName(), target.getPort());
        Executor executor = Executor.newInstance()
                .auth(authScope, defaultcreds)
                .authPreemptive(target);
        try {
            Object response = executor.execute(Request.Get(goCdConfig.getUrl())).handleResponse(new GoCdResponseHandler());

            Metric metric = (Metric) Class.forName("kuona.gocd.analyser.metric." + metricConfig.getMetricType()).newInstance();
            metric.setKuonaAppConfig(kuonaAppConfig);
            metric.setMetricConfig(metricConfig.getConfig());
            metric.analyze((String) response);
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException ie) {
            ie.printStackTrace();
        }
    }
}