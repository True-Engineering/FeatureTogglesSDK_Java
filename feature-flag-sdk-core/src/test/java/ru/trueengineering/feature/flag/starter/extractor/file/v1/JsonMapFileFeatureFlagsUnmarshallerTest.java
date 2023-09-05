package ru.trueengineering.feature.flag.starter.extractor.file.v1;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagFormat;
import ru.trueengineering.feature.flag.starter.extractor.file.BaseJsonFileUnmarshallerSpec;

import java.util.Collections;
import java.util.List;

public class JsonMapFileFeatureFlagsUnmarshallerTest implements BaseJsonFileUnmarshallerSpec {

    @Autowired
    @Getter
    private JsonMapFileFeatureFlagsUnmarshaller uut;

    @Override
    public List<FeatureFlagFormat> validFormats() {
        return Collections.singletonList(FeatureFlagFormat.VERSION_1);
    }
}
