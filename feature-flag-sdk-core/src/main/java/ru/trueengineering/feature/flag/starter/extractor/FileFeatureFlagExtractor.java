package ru.trueengineering.feature.flag.starter.extractor;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.extractor.file.FileFeatureFlagsUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author s.sharaev
 */
@Slf4j
@ToString
@RequiredArgsConstructor
public class FileFeatureFlagExtractor implements FeatureFlagExtractor {

    private final FileFeatureFlagsUnmarshaller featureFlagsUnmarshaller;
    private final Path featureFlagFilePath;

    @Override
    public FeatureFlags getFeatureFlags() throws IOException {
            return featureFlagsUnmarshaller.unmarshal(featureFlagFilePath);
    }
}
