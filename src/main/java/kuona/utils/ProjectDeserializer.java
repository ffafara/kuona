package kuona.utils;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.model.BaseModel;
import kuona.model.Project;
import kuona.utils.Deserializer;
import kuona.utils.PersistentInputStream;

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
            PersistentInputStream tee = new PersistentInputStream(project.openOutputSteam(path, cls), content);
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
