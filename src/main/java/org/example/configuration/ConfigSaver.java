package org.example.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigSaver {

    public static void saveConfig(Map<String, String> config, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), config);
    }
    public static Map<String, String> loadConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Map.class);
    }
}
