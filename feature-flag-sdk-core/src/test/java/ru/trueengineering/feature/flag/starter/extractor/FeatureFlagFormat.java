package ru.trueengineering.feature.flag.starter.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author m.yastrebov
 */
@Getter
@AllArgsConstructor
public enum FeatureFlagFormat {

    VERSION_1("src/test/resources/json/v1/featureFlag.json") {
        @Override
        @SneakyThrows
        public ConfigMap toConfigMap(ObjectMapper objectMapper) {
            final Map<String, FeatureFlag> featureFlags = objectMapper
                    .readerForMapOf(FeatureFlag.class)
                    .readValue(toPath().toFile());

            Map<String, String> data = new HashMap<>();
            featureFlags.entrySet()
                    .forEach(entry -> {
                        try {
                            data.put(entry.getKey(), objectMapper.writeValueAsString(entry.getValue()));
                        } catch (JsonProcessingException e) {
                        }
                    });

            final ConfigMap configMap = new ConfigMap();
            configMap.setData(data);
            return configMap;
        }
    },

    VERSION_2("src/test/resources/json/v2/featureFlag.json") {
        @Override
        @SneakyThrows
        public ConfigMap toConfigMap(ObjectMapper objectMapper) {
            Map<String, String> data = new HashMap<>();
            data.put("featureFlags.json", new String(Files.readAllBytes(toPath())));

            final ConfigMap configMap = new ConfigMap();
            configMap.setData(data);
            return configMap;
        }
    };

    private final String jsonPath;

    public Path toPath() {
        return Paths.get(this.jsonPath);
    }

    public abstract ConfigMap toConfigMap(ObjectMapper objectMapper);
}
