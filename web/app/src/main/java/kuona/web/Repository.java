package kuona.web;

import kuona.web.model.Project;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

public class Repository {

    private final Client client;

    public Repository(Settings settings) {
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("192.168.99.100", 9300));
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

    public Object getMetricConfig(String metric) {

        final GetResponse data = client.prepareGet("kuona", "metricconfig", metric)
                .execute()
                .actionGet();
        return data.getSourceAsMap();
    }

    public void saveMetricData(String metric, String data) {
        IndexResponse response = client.prepareIndex("metric", metric)
                .setCreate(true)
                .setSource(data)
                .execute()
                .actionGet();
        if (!response.isCreated()) {
            //TODO: throw/log error?
        }
    }

    public void saveMetricRawData(String metric, String data) {
        IndexResponse response = client.prepareIndex("rawdata", metric)
                .setCreate(true)
                .setTimestamp(new DateTime().toString())
                .setSource(data)
                .execute()
                .actionGet();
        if (!response.isCreated()) {
            //TODO: throw/log error?
        }
    }

    public Object getMetric(String metric) {
        SearchResponse response = client.prepareSearch("metric")
                .setTypes(metric)
                .setSize(1)
                .addSort("timestamp", SortOrder.DESC)
                .execute()
                .actionGet();

        SearchHits hits = response.getHits();

        return hits.getHits()[0].getSource();
    }

}
