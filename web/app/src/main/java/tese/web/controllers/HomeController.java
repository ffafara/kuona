package tese.web.controllers;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import spark.Request;
import spark.Response;
import spark.Route;


public class HomeController implements Route {
    private Settings settings;

    public HomeController(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        final GetResponse data = client.prepareGet("kuona", "config", "1")
                .execute()
                .actionGet();
        return data.getSourceAsMap();
    }
}
