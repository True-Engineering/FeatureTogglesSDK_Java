package ru.trueengineering.feature.flag.starter.extractor;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.trueengineering.feature.flag.starter.extractor.k8s.BaseKubernetesConfigMapUnmarshallerSpec;
import ru.trueengineering.feature.flag.starter.extractor.k8s.v1.ConfigMapFeatureFlagsExtractor;

import java.util.Collections;
import java.util.List;


public class ConfigMapFeatureFlagsExtractorTest implements BaseKubernetesConfigMapUnmarshallerSpec {

    @Getter
    @Autowired
    private ConfigMapFeatureFlagsExtractor uut;

    @Override
    public List<FeatureFlagFormat> validFormats() {
        return Collections.singletonList(FeatureFlagFormat.VERSION_1);
    }
}
