package ru.trueengineering.feature.flag.starter.extractor.file.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.extractor.file.FileFeatureFlagsUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeaturesHolder;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author m.yastrebov
 */
@Slf4j
@AllArgsConstructor
public class FeaturesHolderFileFeatureFlagsUnmarshaller implements FileFeatureFlagsUnmarshaller {

    private final ObjectMapper objectMapper;

    @Override
    public FeatureFlags unmarshal(Path source) throws IOException {
        return objectMapper.readValue(source.toFile(), FeaturesHolder.class).convertToFlags();
    }
}
