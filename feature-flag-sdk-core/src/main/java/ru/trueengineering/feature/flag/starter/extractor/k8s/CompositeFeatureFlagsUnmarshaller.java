package ru.trueengineering.feature.flag.starter.extractor.k8s;

import io.fabric8.kubernetes.api.model.ConfigMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public final class CompositeFeatureFlagsUnmarshaller implements KubernetesFeatureFlagConfigMapUnmarshaller {

    private final List<KubernetesFeatureFlagConfigMapUnmarshaller> unmarshallers;

    @Override
    public FeatureFlags unmarshal(ConfigMap source) throws Exception {
        List<Exception> delayedExceptions = new ArrayList<>(unmarshallers.size());
        for (KubernetesFeatureFlagConfigMapUnmarshaller unmarshaller : unmarshallers) {
            try {
                return unmarshaller.unmarshal(source);
            } catch (Exception e) {
                delayedExceptions.add(e);
            }
        }
        delayedExceptions.forEach(e ->
                log.error("Unable to fetch feature flags from config map {} with unmarshallers", source, e)
        );
        throw new RuntimeException(String.format("Unable to fetch feature flags from configmap %s", source.toString()));
    }
}
