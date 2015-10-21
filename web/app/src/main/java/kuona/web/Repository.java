package kuona.web;

import kuona.web.model.Project;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class Repository {

    private final Client client;

    public Repository(Settings settings) {
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
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
}
