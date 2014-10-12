package kuona.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;


public class KuonaServer {
    private final String sitePath;

    public KuonaServer(String sitePath) {

        this.sitePath = sitePath;
    }

    public void start() {
        try {
            Server server = new Server(8080);

            TemplateHandler templateHandler = new TemplateHandler(sitePath);

            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[]{"index.html"});

            resource_handler.setResourceBase(sitePath);

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{templateHandler, resource_handler, new DefaultHandler()});
            server.setHandler(handlers);

            server.start();
            server.join();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
