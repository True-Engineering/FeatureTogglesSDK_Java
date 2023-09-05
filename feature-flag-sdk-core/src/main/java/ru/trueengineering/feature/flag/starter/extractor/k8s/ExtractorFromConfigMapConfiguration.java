package ru.trueengineering.feature.flag.starter.extractor.k8s;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.trueengineering.feature.flag.starter.extractor.ObjectMapperBuilder;
import ru.trueengineering.feature.flag.starter.extractor.k8s.v1.ConfigMapFeatureFlagsExtractor;
import ru.trueengineering.feature.flag.starter.extractor.k8s.v2.FeatureFlagsUnmarshaller;

import java.util.List;

/**
 * @author m.yastrebov
 */
@Configuration
public class ExtractorFromConfigMapConfiguration {

    @Bean
    @Primary
    public CompositeFeatureFlagsUnmarshaller compositeFeatureFLagsFromKubernetesUnmarshaller(
            List<KubernetesFeatureFlagConfigMapUnmarshaller> unmarshallers
    ) {
        return new CompositeFeatureFlagsUnmarshaller(unmarshallers);
    }

    @Bean
    public ConfigMapFeatureFlagsExtractor configMapFeatureFlagsExtractor() {
        return new ConfigMapFeatureFlagsExtractor(ObjectMapperBuilder.build());
    }

    @Bean
    public FeatureFlagsUnmarshaller featureFlagsUnmarshaller() {
        return new FeatureFlagsUnmarshaller(ObjectMapperBuilder.build());
    }

}
