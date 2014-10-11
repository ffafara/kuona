package kuona;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateHandler extends AbstractHandler {
    private final String sitePath;

    public TemplateHandler(String sitePath) {

        this.sitePath = sitePath;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
        if (templated(target)) {

            String dashboardFilepath = sitePath + File.separatorChar + "dashboard.json";

            DashboardReader reader = new DashboardReader();

            DashboardModel dashboard = reader.read(new FileInputStream(new File(dashboardFilepath)));

            String templateText = FileUtils.readFileToString(new File(templatePathForTarget(target)));

            ST template = new ST(templateText, '$', '$');
            template.add("dashboard", dashboard);

            response.setStatus(200);
            response.setContentType("text/html");
            String rendered = template.render();
            response.getOutputStream().write(rendered.getBytes());
            request.setHandled(true);
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