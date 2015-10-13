package kuona.client;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

public class PostResults {
    private static final String USER_AGENT = "Mozilla/5.0";
    private final URL resultUri;

    public PostResults(URL resultUri) {
        this.resultUri = resultUri;
    }

    DataOutputStream open() {
        try {
            HttpsURLConnection con = (HttpsURLConnection) resultUri.openConnection();


            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            con.setDoOutput(true);
            return new DataOutputStream(con.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
