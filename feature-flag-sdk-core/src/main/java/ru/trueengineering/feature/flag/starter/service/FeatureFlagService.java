package ru.trueengineering.feature.flag.starter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsWithHash;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {
    private final FeatureFlagsHolder featureFlagsHolder;

    public FeatureFlagsWithHash getFeatureFlagsWithHash() {
        FeatureFlags featureFlags = featureFlagsHolder.getFeatureFlags();
        return new FeatureFlagsWithHash(
                featureFlags.getFeatureMap(),
                featureFlags.getFlagsHash()
        );
    }

    public FeatureFlagsWithHash getFeatureFlagsWithHashByTag(String tag) {
        FeatureFlags featureFlags = featureFlagsHolder.getFeatureFlagsByTag(tag);
        return new FeatureFlagsWithHash(
                featureFlags.getFeatureMap(),
                featureFlags.getFlagsHash()
        );
    }
}