package kuona.utils;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.model.BaseModel;

import java.io.InputStream;

public class Deserializer {
    public  <T extends BaseModel> T objectFromResponse(Class<T> cls, InputStream content) {
        try {
            final ObjectMapper mapper = getDefaultMapper();
            return mapper.readValue(content, cls);
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
