package kuona.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;


@WebSocket
public class StatusSocket {
    private Session session;
    private Thread ticker;


    @OnWebSocketConnect
    public void connected(Session session) {
        this.session = session;
        ticker = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    session.getRemote().sendString("{\n" +
                            "  \"status\": \"running\"\n" +
                            "}");
                }
            } catch (InterruptedException | IOException e) {
                System.err.println("Error in ticker.");
            }
        });
        ticker.start();
    }

    @OnWebSocketClose
    public void closed(int statusCode, String reason) {
        ticker.interrupt();
        ticker = null;
        this.session = null;
    }

    @OnWebSocketMessage
    public void message(String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }
}
