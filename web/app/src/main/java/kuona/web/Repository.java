package kuona.web;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import kuona.web.model.Metric;
import kuona.web.model.MetricConfig;
import kuona.web.model.Project;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.MergeMappingException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


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
            createIndicesAndMappings();

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
        client.prepareIndex("metricsdata", "metric", m.getId())
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
        SearchResponse response = client.prepareSearch("metricsdata")
                .setTypes("metric")
                .setQuery(QueryBuilders.termQuery("name", metric))
                .setSize(1)
                .addSort("timestamp", SortOrder.DESC)
                .execute()
                .actionGet();

        SearchHit[] hits = response.getHits().getHits();
        return hits.length > 0 ? hits[0].getSource() : null;
    }

    public List<MetricConfig> getMetricConfigList() {
        SearchResponse response = client.prepareSearch("kuona")
                .setTypes("metricconfig")
//                .setSearchType(SearchType.SCAN)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute()
                .actionGet();

        SearchHit[] hits = response.getHits().getHits();

        Gson gson = new Gson();
        List<MetricConfig> configList = new ArrayList<>();
        for (SearchHit hit: hits) {
            try {
                MetricConfig metricConfig = gson.fromJson(hit.getSourceAsString(), MetricConfig.class);
                configList.add(metricConfig);
            } catch (JsonSyntaxException e) {
                System.out.println("Unable to parse metric config from elasticsearch!\n" + e.toString());
            }
        }
        return configList;
    }

    protected void createIndicesAndMappings() {
        try {
            client.admin().indices().create(new CreateIndexRequest("kuona-metrics"));

            client.admin().indices()
                    .preparePutMapping("kuona-metrics")
                    .setType("_default_")
                    .setSource(jsonBuilder().prettyPrint()
                            .startObject()
                            .startObject("properties")
                            .startObject("name").field("type", "string").field("index", "not_analyzed").endObject()
                            .startObject("timestamp").field("type", "date").endObject()
                            .endObject()
                            .endObject())
                    .execute();

        } catch (IOException | MergeMappingException e) {
            e.printStackTrace();
        }
    }
}
