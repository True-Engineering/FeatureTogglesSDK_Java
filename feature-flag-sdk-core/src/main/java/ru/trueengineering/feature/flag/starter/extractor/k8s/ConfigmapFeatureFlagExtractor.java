package ru.trueengineering.feature.flag.starter.extractor.k8s;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.kubernetes.config.ConfigMapConfigProperties;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import static java.util.Objects.nonNull;

/**
 * @author s.sharaev
 */
@Slf4j
@ToString
@RequiredArgsConstructor
public class ConfigmapFeatureFlagExtractor  implements FeatureFlagExtractor {

    private final KubernetesClient kubernetesClient;
    private final KubernetesFeatureFlagConfigMapUnmarshaller configMapUnmarshaller;
    private final ConfigMapConfigProperties configMapConfigProperties;

    @Override
    public FeatureFlags getFeatureFlags() throws Exception {
        String configMapName = configMapConfigProperties.getName();
        ConfigMap configMap;
        try {
            configMap = kubernetesClient.configMaps().withName(configMapName).get();
        } catch (Exception e) {
            log.error("Failed to read configmap {}", configMapName, e);
            throw e;
        }
        if (nonNull(configMap)) {
            try {
                return configMapUnmarshaller.unmarshal(configMap);
            } catch (Exception e) {
                log.error("Error to read config map {}!", configMap);
                throw e;
            }
        }
        throw new RuntimeException(String.format("Unable to fetch feature flags from configmap %s", configMapName));
    }
}
