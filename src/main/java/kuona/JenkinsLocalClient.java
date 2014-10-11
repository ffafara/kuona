package kuona;

import com.offbytwo.jenkins.model.BaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class JenkinsLocalClient implements JenkinsClient {


    private Project project;
    private JenkinsClient delegate;

    public JenkinsLocalClient(Project project, JenkinsClient delegate) {
        this.project = project;
        this.delegate = delegate;
    }

    @Override
    public URI getURI() {
        return delegate.getURI();
    }

    @Override
    public String get(String path) throws IOException {
        if (project.exists(path))
            return project.read(path);
        else
            return delegate.get(path);
    }

    @Override
    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException {
        if (project.exists(path)) {
            final T t = project.get(path, cls);
            t.setClient(this);
            return t;
        }
        return delegate.get(path, cls);
    }

    @Override
    public InputStream getFile(URI path) throws IOException {
        return null;
    }
}
