package ru.trueengineering.feature.flag.starter.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContextHolder;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsStateWithHash;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategy;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategyProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;

/**
 * @author s.sharaev
 */
@Slf4j
@RequiredArgsConstructor
public class MapFeatureFlagStateProvider implements FeatureFlagStateProvider {
    private static final String TAG_KEY = "tag";
    private static final String TAG_DELIMITER = ",";

    private final FeatureFlagsHolder featureFlagsHolder;
    private final FeatureFlagContextHolder featureFlagContextHolder;
    private final FeatureFlagStrategyProvider featureFlagStrategyProvider;
    private final FeatureFlagScanNamesProperties scanNamesProperties;


    @Override
    public boolean check(@NonNull String featureName) {
        boolean featureState = getFeatureFlag(featureName)
                .map(featureFlag -> featureFlag.isEnable() && checkStrategy(featureFlag))
                .orElse(false);

        log.debug("State of feature {} = {}", featureName, featureState ? "enabled" : "disabled");
        return featureState;
    }

    private boolean checkStrategy(FeatureFlag featureFlag) {
        if (featureFlag.getFlippingStrategy() == null) {
            return true;
        }

        FeatureFlagStrategy featureFlagStrategy = featureFlagStrategyProvider.getStrategy(
                featureFlag.getFlippingStrategy().getClassName());
        if (featureFlagStrategy == null) {
            log.warn("Unable to find strategy {} for feature flag {}",
                    featureFlag.getFlippingStrategy().getClassName(),
                    featureFlag.getUid());
            return true;
        }
        return featureFlagStrategy
                .evaluate(featureFlagContextHolder.getFeatureFlagContext(),
                        featureFlag.getFlippingStrategy().getInitParams());
    }

    @Override
    public Map<String, Boolean> getByTag(String tag) {
        if (isNull(tag)) {
            return emptyMap();
        }

        return featureFlagsHolder.getFeatureFlags().getFeatureMap()
                .entrySet()
                .stream()
                .filter(flagEntry -> containsTag(flagEntry.getValue(), tag))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> check(e.getKey())
                ));
    }

    @Override
    public FeatureFlagsStateWithHash getWithHashByTag(String tag) {
        Map<String, Boolean> values = getByTag(tag);
        return new FeatureFlagsStateWithHash(values, values.isEmpty() ?
                Strings.EMPTY : featureFlagsHolder.getFeatureFlags().getFlagsHash());
    }

    private boolean containsTag(FeatureFlag featureFlag, String tag) {
        String tags = featureFlag.getCustomProperties().get(TAG_KEY);
        return tags != null && Arrays.asList(
                tags.split(TAG_DELIMITER)
        ).contains(tag);
    }

    private Optional<FeatureFlag> getFeatureFlag(String featureName) {
        if (scanNamesProperties.isEnabled()) {
            checkFeatureExist(featureName);
        }

        return Optional.ofNullable(featureName)
                .map(name -> featureFlagsHolder.getFeatureFlags().getFeatureMap().get(name));
    }

    private void checkFeatureExist(String featureName) {
        if (featureName == null) {
            return;
        }

        if (!featureFlagsHolder.hasFeatureFlag(featureName)) {
            log.warn("Feature flag " + featureName + " is not found in the holder");
        }
    }
}
