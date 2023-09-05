package ru.trueengineering.feature.flag.starter.extractor;

import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

/**
 * @author s.sharaev
 */
public interface FeatureFlagExtractor {

    FeatureFlags getFeatureFlags() throws Exception;
}
