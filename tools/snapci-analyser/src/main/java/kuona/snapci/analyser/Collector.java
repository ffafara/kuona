package kuona.snapci.analyser;

import kuona.snapci.analyser.model.Pipeline;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import kuona.snapci.analyser.utils.Deserializer;
import kuona.snapci.analyser.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Collector implements Job {

    private final static String KUONA_RAWDATA_INDEX = "rawdata";
    private final static String SNAP_DATA_TYPE = "snap-pipeline";
    private final static String KUONA_METRICS_INDEX = "kuona-metrics";

    SnapConfig snapConfig;
    ElasticSearchConfig elasticSearchConfig;

    public void setElasticSearchConfig(ElasticSearchConfig elasticSearchConfig) {
        this.elasticSearchConfig = elasticSearchConfig;
    }

    public void setSnapConfig(SnapConfig snapConfig) {
        this.snapConfig = snapConfig;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Utils.puts("Snap-Ci Collector Job running...");
        try {
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("cluster.name", elasticSearchConfig.getClusterName()).build();
            Client elasticClient = new TransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(elasticSearchConfig.getHost(), elasticSearchConfig.getPort()));

            URI uri = new URI(snapConfig.getUrl());
            HttpHost target = URIUtils.extractHost(uri);
            Credentials defaultcreds = new UsernamePasswordCredentials(snapConfig.getUser(), snapConfig.getPassword());
            AuthScope authScope = new AuthScope(target.getHostName(), target.getPort());
            Executor executor = Executor.newInstance()
                    .auth(authScope, defaultcreds)
                    .authPreemptive(target);

            try {
                Object response = executor.execute(Request.Get(snapConfig.getUrl())).handleResponse(new SnapResponseHandler());

                saveRawData(elasticClient, (String)response);

                // Parse json to class
                Pipeline pipeline = new Deserializer().objectFromString(Pipeline.class, (String)response);

                // Write stages info to ElasticSearch
                pipeline.getStages().forEach(stage -> {
                    try {
                        elasticClient.prepareIndex(KUONA_METRICS_INDEX, "stagebuild")
                                .setSource(jsonBuilder()
                                                .startObject()
                                                .field("date", new Date())
                                                .field("name", stage.getName())
                                                .field("project", stage.getProjectName())
                                                .field("result", stage.getResult())
                                                .endObject()
                                )
                                .execute();
                    } catch (IOException e) {
                        Utils.puts(e.toString());
                        e.printStackTrace();
                    }
                });

                // Write pipeline info to ElasticSearch
                elasticClient.prepareIndex("kuona-metrics", "pipelinebuilstatus")
                        .setSource(jsonBuilder()
                                        .startObject()
                                        .field("name", "Palace-Intrigue")
                                        .field("buildserver", "snap")
                                        .field("status", pipeline.getResult())
                                        .endObject()
                        )
                        .execute();

            } catch (IOException ie) {
                Utils.puts(ie.toString());
                ie.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void saveRawData(Client elasticClient, String data) {
        try {
            elasticClient.prepareIndex(KUONA_RAWDATA_INDEX, SNAP_DATA_TYPE)
                    .setSource(data)
                    .execute()
                    .actionGet();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
    }

}
