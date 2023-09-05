package ru.trueengineering.feature.flag.starter.extractor.k8s.v2;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagFormat;
import ru.trueengineering.feature.flag.starter.extractor.k8s.BaseKubernetesConfigMapUnmarshallerSpec;

import java.util.Collections;
import java.util.List;

/**
 * @author m.yastrebov
 */
class FileFeatureFlagsUnmarshallerTest implements BaseKubernetesConfigMapUnmarshallerSpec {

    @Autowired
    @Getter
    private FeatureFlagsUnmarshaller uut;

    @Override
    public List<FeatureFlagFormat> validFormats() {
        return Collections.singletonList(FeatureFlagFormat.VERSION_2);
    }
}
