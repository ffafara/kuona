package kuona.web;

import kuona.web.model.Metric;
import kuona.web.model.Project;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Repository {

    private Client client;

    public Repository(Settings settings) {
        client = null;
        try {
            client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Object getConfig() {
        final GetResponse data = client.prepareGet("kuona", "config", "1")
                .execute()
                .actionGet();
        return data.getSourceAsMap();
    }

    public void save(Project p) {
        client.prepareIndex("kuona", "project", p.getName()).setCreate(true).setSource("name", p.getName(), "description", p.getDescription()).execute();
    }

    public void save(Metric m) {
        client.prepareIndex("kuona", "metric", m.getId())
                .setCreate(true)
                .setContentType(XContentType.JSON)
                .setSource(m.getData()).execute();
    }
}
