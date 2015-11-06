package kuona.snapci.analyser;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import java.io.IOException;

public class SnapResponseHandlerTest {

    @Test(expected = HttpResponseException.class)
    public void shouldThrowExceptionOnErrorStatusCode() throws IOException {
        HttpResponse res = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP",1,1),404,null));
        SnapResponseHandler handler = new SnapResponseHandler();
        handler.handleResponse(res);
    }

    @Test(expected = ClientProtocolException.class)
    public void shouldThrowExceptionOnEmptyResponse() throws IOException {
        HttpResponse res = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP",1,1),200,null));
        SnapResponseHandler handler = new SnapResponseHandler();
        handler.handleResponse(res);
    }

}