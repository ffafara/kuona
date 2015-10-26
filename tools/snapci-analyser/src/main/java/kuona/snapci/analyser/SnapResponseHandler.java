package kuona.snapci.analyser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SnapResponseHandler implements ResponseHandler {

    @Override
    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        StatusLine statusLine = httpResponse.getStatusLine();
        HttpEntity entity = httpResponse.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        try {
            ContentType contentType = ContentType.getOrDefault(entity);
            if (!contentType.toString().contains("application/vnd.snap-ci.com.v1+json")) {
                throw new ClientProtocolException("Unexpected content type:" +
                        contentType);
            }

            // Get Raw String
            String rawStringResponseContent;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"))) {
                rawStringResponseContent = reader.lines().collect(Collectors.joining("\n"));
            }

            return rawStringResponseContent;

        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }

    }
}
