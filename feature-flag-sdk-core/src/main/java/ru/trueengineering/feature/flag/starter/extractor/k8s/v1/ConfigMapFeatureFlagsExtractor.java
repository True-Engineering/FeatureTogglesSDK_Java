package ru.trueengineering.feature.flag.starter.extractor.k8s.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.extractor.k8s.KubernetesFeatureFlagConfigMapUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.util.HashMap;
import java.util.Map;

/**
 * Извлекает фичефлаги из конфигмапы
 */
@Slf4j
@AllArgsConstructor
public class ConfigMapFeatureFlagsExtractor implements KubernetesFeatureFlagConfigMapUnmarshaller {

    private final ObjectMapper objectMapper;

    private static FeatureFlag featureFlagFromString(ObjectMapper objectMapper, String value) {
        try {
            return objectMapper.readValue(value, FeatureFlag.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    String.format("Can't serialize featureFlag from string %s", value),
                    e);
        }
    }

    @Override
    public FeatureFlags unmarshal(ConfigMap source) {
        if (source == null) {
            throw new RuntimeException("Unable to fetch feature flags, configmap is null!");
        }

        Map<String, FeatureFlag> featureMap = new HashMap<>();
        for (Map.Entry<String, String> entry : source.getData().entrySet()) {
            FeatureFlag featureFlag = featureFlagFromString(objectMapper, entry.getValue());
            featureMap.put(entry.getKey(), featureFlag);
        }

        return new FeatureFlags(featureMap);
    }
}
