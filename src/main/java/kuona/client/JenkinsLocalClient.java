package kuona.client;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.model.BaseModel;
import kuona.model.Mergable;
import kuona.model.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
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
        return null;
//        if (project.exists(path)) {
//            try {
//                return FileUtils.readFileToString(new File(project.contentPath(path, contentType)));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else
//            return delegate.get(path, contentType);
    }

    @Override
    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException {
        if (project.exists(path, cls)) {
            try {
                final FileInputStream inputStream = FileUtils.openInputStream(new File(project.contentPath(path, cls.getSimpleName())));
                T t = objectFromResponse(cls, inputStream);
                t.setClient(this);

                if (Mergable.class.isAssignableFrom(cls)) {
                    T other = delegate.get(path, cls);
                    return ((Mergable<T>) t).merge(other);
                }

                return t;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return delegate.get(path, cls);
    }

    private <T extends BaseModel> T objectFromResponse(Class<T> cls, InputStream content) throws IOException {
        final ObjectMapper mapper = getDefaultMapper();
        return mapper.readValue(content, cls);
    }

    private ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }


    @Override
    public InputStream getFile(URI path) throws IOException {
        return null;
    }
}
