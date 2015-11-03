package kuona.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostResults {
    private static final String USER_AGENT = "Mozilla/5.0";
    private final URL resultUri;

    public PostResults(URL resultUri) {
        this.resultUri = resultUri;
    }

    public void post(byte[] data) {
        try {
            HttpURLConnection con = (HttpURLConnection) resultUri.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoOutput(true);
            final DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());

            outputStream.write(data);
            outputStream.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader((con.getInputStream()), StandardCharsets.UTF_8));
            System.out.println(reader.readLine());
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to post to " + resultUri + '\n' + e.toString(), e);
        }

    }
}
