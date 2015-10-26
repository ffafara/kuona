package kuona.snapci.analyser.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class Deserializer {

    public  <T> T objectFromResponse(Class<T> cls, InputStream content) {
        try {
            final ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(content, cls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T objectFromString(Class<T> cls, String content) {
        try {
            final ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(content, cls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

