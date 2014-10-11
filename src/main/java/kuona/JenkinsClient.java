package kuona;

import com.offbytwo.jenkins.model.BaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface JenkinsClient {

    URI getURI();

    public String get(String path) throws IOException;

    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException;

    InputStream getFile(URI path) throws IOException;
}
