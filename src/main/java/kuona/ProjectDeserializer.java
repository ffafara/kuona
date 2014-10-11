package kuona;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.model.BaseModel;

import java.io.InputStream;

public class ProjectDeserializer extends Deserializer {
    private Project project;
    private String path;

    public ProjectDeserializer(Project project, String path) {
        this.project = project;
        this.path = path;
    }

    @Override
    public  <T extends BaseModel> T objectFromResponse(Class<T> cls, InputStream content) {
        try {
            PersistentInputStream tee = new PersistentInputStream(project.openOutputSteam(path), content);
            final ObjectMapper mapper = getDefaultMapper();
            return mapper.readValue(tee, cls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

}
