package ru.trueengineering.feature.flag.starter.extractor.file.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import ru.trueengineering.feature.flag.starter.extractor.file.FileFeatureFlagsUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author m.yastrebov
 */
@AllArgsConstructor
public class JsonMapFileFeatureFlagsUnmarshaller implements FileFeatureFlagsUnmarshaller {

    private final ObjectMapper objectMapper;

    @Override
    public FeatureFlags unmarshal(Path source) throws IOException {
        return new FeatureFlags(objectMapper
                .readerForMapOf(FeatureFlag.class)
                .readValue(source.toFile())
        );
    }
}
