package ru.trueengineering.feature.flag.starter.configuration;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.core.v1.ConfigMapOperationsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.ApplicationContextAssert;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagConfigMapChangeDetector;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagFileChangeDetector;
import ru.trueengineering.feature.flag.starter.extractor.k8s.KubernetesFeatureFlagConfigMapUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author m.yastrebov
 */
class FeatureFlagContextPropagationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    FeatureFlagClientConfiguration.class, FeatureFlagContextPropagationAutoConfiguration.class
            ));

    @Test
    void shouldRunAutoConfigurationWithPropertiesFileFeatureFlagStore() throws Exception {
        contextRunner.withPropertyValues(
                "feature.flag.store.type=file",
                "feature.flag.store.file.name=src/test/resources/feature-flags.json",
                "feature.flag.store.file.reload-period=1000"
        ).run((context) -> {
            final ApplicationContextAssert<ConfigurableApplicationContext> assertContext =
                    AssertableApplicationContext.get(() -> context).assertThat();
            assertContext.hasBean("featureFlagsHolder");
            assertContext.hasSingleBean(FeatureFlagFileChangeDetector.class);
            assertContext.doesNotHaveBean(FeatureFlagConfigMapChangeDetector.class);

            final FeatureFlagsHolder featureFlagsHolder =
                    context.getBean("featureFlagsHolder", FeatureFlagsHolder.class);
            assertThat(featureFlagsHolder.getFeatureFlags().getFeatureMap()).containsKey("feature.id");
        });
    }

    @Test
    void shouldRunAutoConfigurationWithConfigMapFeatureFlagStore() throws Exception {
        KubernetesClient kubernetesClient = mock(KubernetesClient.class);
        KubernetesFeatureFlagConfigMapUnmarshaller configMapUnmarshaller =
                mock(KubernetesFeatureFlagConfigMapUnmarshaller.class);
        ConfigMapOperationsImpl configmaps = mock(ConfigMapOperationsImpl.class);
        when(kubernetesClient.configMaps()).thenReturn(configmaps);
        ConfigMap configMap = mock(ConfigMap.class);
        when(configmaps.withName("feature-flag-cm")).thenReturn(configmaps);
        when(configmaps.get()).thenReturn(configMap);
        HashMap<String, FeatureFlag> featureMap = new HashMap<>();
        featureMap.put("feature", new FeatureFlag());
        FeatureFlags featureFlags = new FeatureFlags(featureMap);
        when(configMapUnmarshaller.unmarshal(configMap)).thenReturn(featureFlags);
        contextRunner
                .withBean(KubernetesClient.class, () -> kubernetesClient)
                .withBean(KubernetesFeatureFlagConfigMapUnmarshaller.class, () -> configMapUnmarshaller)
                .withPropertyValues(
                        "feature.flag.store.type=configmap",
                        "spring.cloud.kubernetes.config.name=feature-flag-cm"
                ).run((context) -> {
                    final ApplicationContextAssert<ConfigurableApplicationContext> assertContext =
                            AssertableApplicationContext.get(() -> context).assertThat();
                    assertContext.hasBean("featureFlagsHolder");
                    assertContext.doesNotHaveBean(FeatureFlagFileChangeDetector.class);
                    assertContext.hasSingleBean(FeatureFlagConfigMapChangeDetector.class);

                    final FeatureFlagsHolder featureFlagsHolder =
                            context.getBean("featureFlagsHolder", FeatureFlagsHolder.class);
                    assertThat(featureFlagsHolder).isNotNull();
                    assertThat(featureFlagsHolder.getFeatureFlags()).isEqualTo(featureFlags);
                });
    }
}
