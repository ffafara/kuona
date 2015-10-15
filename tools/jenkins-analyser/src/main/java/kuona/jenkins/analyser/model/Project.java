package kuona.jenkins.analyser.model;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.jenkins.analyser.utils.ProjectDeserializer;
import kuona.jenkins.analyser.utils.Deserializer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class Project {
    private final URI url;
    private String name;

    public Project(URI url) {
        this.url = url;
        this.name = url.getHost();
    }

    public String getName() {
        return name;
    }

    public OutputStream openOutputSteam(String path, Class cls) {
        return openOutputSteam(path, cls.getSimpleName());
    }

    public OutputStream openOutputSteam(String path, String contentType) {
        String contentPath = contentPath(path, contentType);

        try {
            return FileUtils.openOutputStream(new File(contentPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String contentPath(String path, String name) {
        final String relativePath = relativePath(path);
        return (relativePath.endsWith("/")) ? relativePath + name : relativePath + File.separatorChar + name;
    }

    private String relativePath(String path) {
        String cleanPath = cleanPath(path);
        return (cleanPath.startsWith("/") || name.endsWith("/")) ? name + cleanPath : name + File.separatorChar + cleanPath;
    }

    private String cleanPath(String path) {
        if (path.startsWith(url.toString())) {
            return path.substring(url.toString().length());
        }
        return path;
    }

    public boolean exists(String path, Class cls) {
        return new File(contentPath(path, cls.getSimpleName())).exists();
    }

    public boolean exists(String path, String contentType) {
        return new File(contentPath(path, contentType)).exists();
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


    public Deserializer getDeserializerFor(String path) {
        return new ProjectDeserializer(this, path);
    }
}
