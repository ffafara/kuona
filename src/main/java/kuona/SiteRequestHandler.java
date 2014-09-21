package kuona;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SiteRequestHandler extends AbstractHandler {
    final String _greeting;
    final String _body;


    public SiteRequestHandler(String sitePath) {
        _greeting = sitePath;
        _body = null;
    }


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        response.getWriter().println("<h1>" + _greeting + "</h1>");
        if (_body != null)
            response.getWriter().println(_body);
    }
}
