package kuona.snapci.analyser;

import kuona.snapci.analyser.metric.Metric;
import kuona.snapci.analyser.utils.Utils;
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
        Utils.puts("Snap-Ci Collector Job running...");
        SnapConfig snapConfig = metricConfig.getSnapConfig();
        HttpHost target = snapConfig.getHost();
        Credentials defaultcreds = new UsernamePasswordCredentials(snapConfig.getUser(), snapConfig.getPassword());
        AuthScope authScope = new AuthScope(target.getHostName(), target.getPort());
        Executor executor = Executor.newInstance()
                .auth(authScope, defaultcreds)
                .authPreemptive(target);
        try {
            Object response = executor.execute(Request.Get(snapConfig.getUrl())).handleResponse(new SnapResponseHandler());

            Metric metric = (Metric) Class.forName(metricConfig.getMetricType()).newInstance();
            metric.setKuonaAppConfig(kuonaAppConfig);
            metric.setMetricConfig(metricConfig.getConfig());
            metric.analyze((String) response);

        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException ie) {
            ie.printStackTrace();
        }
    }
}