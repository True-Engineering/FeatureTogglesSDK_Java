package ru.trueengineering.feature.flag.starter.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagStateRefresher;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author s.sharaev
 */
@Slf4j
@RequiredArgsConstructor
public class FeatureFlagsHolder implements FeatureFlagStateRefresher {

    private static final String TAG_KEY = "tag";
    private static final String TAG_DELIMITER = ",";

    private final AtomicReference<FeatureFlags> featureFlags = new AtomicReference<>();

    private final FeatureFlagExtractor featureFlagExtractor;

    @PostConstruct
    public void init() throws Exception {

        try {
            fetchAndUpdateFeatureFlags();
        }
        catch (Exception e) {
            log.error("Unable to init feature flag holder!", e);
            throw e;
        }
    }

    @Override
    public void refreshFeatureFlags() {
        log.debug("Feature flags has been updated, try to save new flags");
        try {
            fetchAndUpdateFeatureFlags();
        } catch (Exception e){
            log.error("Unable to update feature flags!", e);
        }
        log.info("New feature flags have saved!");
    }

    private void fetchAndUpdateFeatureFlags() throws Exception {
        FeatureFlags newFeatureFlags = featureFlagExtractor.getFeatureFlags();
        if (newFeatureFlags != null && newFeatureFlags.getFeatureMap() != null) {
            this.featureFlags.set(newFeatureFlags);
        }
    }

    public FeatureFlags getFeatureFlags() {
        return featureFlags.get();
    }

    public FeatureFlags getFeatureFlagsByTag(String tag) {
        Map<String, FeatureFlag> featureMap = featureFlags.get().getFeatureMap()
                .entrySet()
                .stream()
                .filter(entry -> containsTag(entry.getValue(), tag))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new FeatureFlags(featureMap);
    }

    public boolean hasFeatureFlag(String featureName) {
        return featureFlags.get().getFeatureMap().containsKey(featureName);
    }

    private boolean containsTag(FeatureFlag featureFlag, String tag) {
        String tags = featureFlag.getCustomProperties().get(TAG_KEY);
        return tags != null && Arrays.asList(
                tags.split(TAG_DELIMITER)
        ).contains(tag);
    }
}
