package ru.trueengineering.feature.flag.starter.extractor.k8s.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import lombok.AllArgsConstructor;
import ru.trueengineering.feature.flag.starter.extractor.k8s.KubernetesFeatureFlagConfigMapUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeaturesHolder;

/**
 * @author m.yastrebov
 */
@AllArgsConstructor
public class FeatureFlagsUnmarshaller implements KubernetesFeatureFlagConfigMapUnmarshaller {

    private final ObjectMapper objectMapper;

    @Override
    public FeatureFlags unmarshal(ConfigMap source) throws Exception {
        if (source == null) {
            throw new RuntimeException("Unable to fetch feature flags, configmap is null!");
        }

        final String json = source.getData().get("featureFlags.json");
        if (json == null || json.isEmpty()) {
            throw new RuntimeException(
                    String.format("Unable to fetch feature flags from configmap %s, configMap data is empty", source));
        }

        try {
            return objectMapper.readValue(json, FeaturesHolder.class).convertToFlags();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    String.format("Unable to fetch feature flags from configmap %s, configMap data is invalid", source),
                    e);
        }
    }
}
