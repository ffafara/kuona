package kuona.web;

import kuona.web.model.Metric;
import kuona.web.model.Project;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.Instant;


public class Repository {

    private Client client;

    public Repository(String[] hosts) {
        client = null;
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", "elasticsearch")
                    .build();

            final TransportClient transportClient = TransportClient.builder().settings(settings).build();

            for (String host : hosts) {
                try {
                    URI uri = new URI("my://" + host);

                    transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(uri.getHost()), uri.getPort()));
                } catch (URISyntaxException e) {
                    System.err.println(host + " can't be used as an elastic search endpoint: " + e.getMessage());
                }
            }

            client = transportClient;

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

    public Object getMetricConfig(String metric) {

        final GetResponse data = client.prepareGet("kuona", "metricconfig", metric)
                .execute()
                .actionGet();
        return data.getSourceAsMap();
    }


    public void saveMetricRawData(String metric, String data) {
        IndexResponse response = client.prepareIndex("rawdata", metric)
                .setCreate(true)
                .setTimestamp(Instant.now().toString())
                .setSource(data)
                .execute()
                .actionGet();
        if (!response.isCreated()) {
            //TODO: throw/log error
        }
    }

    public Object getMetric(String metric) {
        SearchResponse response = client.prepareSearch("kuona")
                .setTypes("metric")
                .setQuery(QueryBuilders.matchQuery("name", metric))
                .setSize(1)
                .addSort("timestamp", SortOrder.DESC)
                .execute()
                .actionGet();

        SearchHits hits = response.getHits();

        return hits.getHits()[0].getSource();
    }
}
