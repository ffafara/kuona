package kuona.utils;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuona.model.DashboardModel;

import java.io.IOException;
import java.io.InputStream;

public class DashboardReader {
    public DashboardModel read(InputStream inputStream) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return mapper.readValue(inputStream, DashboardModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
