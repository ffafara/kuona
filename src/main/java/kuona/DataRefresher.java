package kuona;

import kuona.processor.JenkinsServer;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class DataRefresher implements Job {
    private final Client client;
    private static volatile boolean running;

    public DataRefresher() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch_graham").build();
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

//        Node node = nodeBuilder().node();
//        client = node.client();
        running = false;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Refreshing data");
        if (running) {
            return;
        }
        running = true;
        try {
            updateSite();
        } catch (Throwable e) {
            System.err.println("Error updating project data");
        }
        running = false;
        System.out.println("Refreshed");
    }


    private void updateSite() {
        GetResponse response = client.prepareGet("kuona", "config", "1")
                .execute()
                .actionGet();

        final Map<String, Object> document = response.getSource();

        List<Map<String, Object>> projects = (List<Map<String, Object>>) document.get("projects");

        projects.stream().forEach((p) -> {
            final Map<String, String> ci = (Map<String, String>) p.get("ci-server");

            final JenkinsServer server = new JenkinsServer(ci.get("url"), ci.get("username"), ci.get("password"));

            server.collectMetrics(client);
        });
    }
}

