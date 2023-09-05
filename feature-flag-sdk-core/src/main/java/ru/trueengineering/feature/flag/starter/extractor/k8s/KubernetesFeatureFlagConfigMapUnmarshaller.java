package ru.trueengineering.feature.flag.starter.extractor.k8s;

import io.fabric8.kubernetes.api.model.ConfigMap;
import org.springframework.lang.Nullable;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

/**
 * @author m.yastrebov
 */
public interface KubernetesFeatureFlagConfigMapUnmarshaller {

    FeatureFlags unmarshal(@Nullable ConfigMap source) throws Exception;
}
