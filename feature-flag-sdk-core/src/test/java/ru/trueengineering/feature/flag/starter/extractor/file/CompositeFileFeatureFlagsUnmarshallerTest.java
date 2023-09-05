package ru.trueengineering.feature.flag.starter.extractor.file;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagFormat;

import java.util.Arrays;
import java.util.List;

/**
 * @author m.yastrebov
 */
class CompositeFileFeatureFlagsUnmarshallerTest implements BaseJsonFileUnmarshallerSpec {

    @Autowired
    @Getter
    private CompositeFileFeatureFlagsUnmarshaller uut;

    @Override
    public List<FeatureFlagFormat> validFormats() {
        return Arrays.asList(FeatureFlagFormat.values());
    }
}
