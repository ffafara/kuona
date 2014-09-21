package kuona;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static kuona.utils.Utils.puts;

public class TemplateHandler extends AbstractHandler {
    private final String sitePath;

    public TemplateHandler(String sitePath) {

        this.sitePath = sitePath;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
        if (templated(target)) {
            byte[] encoded = Files.readAllBytes(Paths.get(templatePathForTarget(target)));

            ObjectMapper mapper = new ObjectMapper();

            String dashboardFilepath = sitePath + File.separatorChar + "dashboard.json";

            JsonNode node = mapper.readTree(new File(dashboardFilepath));


            ST template = new ST(new String(encoded), '$', '$');
            template.add("dashboard", node);

            template.render();

            response.setStatus(200);
            response.setContentType("text/html");
            response.getOutputStream().write(template.render().getBytes());
        }
    }

    private String templatePathForTarget(String target) {
        if (target.endsWith("/")) {
            String indexPath = sitePath + target + "index.html";
            File index = new File(indexPath);
            if (index.exists()) {
                return indexPath;
            }
        }
        if (target.endsWith(".html")) {
            String indexPath = sitePath + target;
            File index = new File(indexPath);
            if (index.exists()) {
                return indexPath;
            }
        }
        return "";
    }

    private boolean templated(String target) {
        if (target.endsWith("/")) {
            String indexPath = sitePath + target + "index.html";
            File index = new File(indexPath);
            if (index.exists()) {
                return true;
            }
        }
        if (target.endsWith(".html")) {
            String indexPath = sitePath + target;
            File index = new File(indexPath);
            if (index.exists()) {
                return true;
            }
        }
        return false;
    }
}