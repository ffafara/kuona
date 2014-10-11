package kuona;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.model.BaseModel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;

public class Project {
    public static final String CONTENT_FILENAME = "content";
    private String name;

    public Project(URI url) {
        this.name = url.getHost();
    }

    public String getName() {
        return name;
    }

    public OutputStream openOutputSteam(String path) {
        String contentPath = contentPath(path);

        try {
            return FileUtils.openOutputStream(new File(contentPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String contentPath(String path) {
        final String relativePath = relativePath(path);
        return (relativePath.endsWith("/")) ? relativePath + CONTENT_FILENAME : relativePath + File.separatorChar + CONTENT_FILENAME;
    }
    private String relativePath(String path) {
        return (path.startsWith("/") || name.endsWith("/")) ? name + path : name + File.separatorChar + path;
    }

    public boolean exists(String path) {
        return new File(contentPath(path)).exists();
    }

    public <T extends BaseModel> String read(String path) {
        try {
            return FileUtils.readFileToString(new File(contentPath(path)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends BaseModel> T get(String path, Class<T> cls) {

        try {
            final FileInputStream inputStream = FileUtils.openInputStream(new File(contentPath(path)));
            return objectFromResponse(cls, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
