package ru.trueengineering.feature.flag.starter.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsWithHash;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;
import ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.TAGGED_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.UNTAGGED_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.provideFeatureFlags;

public class FeatureFlagServiceTest {
    private static final String ANOTHER_DESCRIPTION = "ANOTHER_DESCRIPTION";
    private final FeatureFlags featureFlags = provideFeatureFlags();

    @Mock
    private FeatureFlagExtractor extractor;
    private FeatureFlagsHolder featureFlagsHolder;
    private FeatureFlagService featureFlagService;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(extractor.getFeatureFlags()).thenReturn(featureFlags);
        featureFlagsHolder = new FeatureFlagsHolder(extractor);
        featureFlagsHolder.refreshFeatureFlags();
        featureFlagService = new FeatureFlagService(featureFlagsHolder);
    }

    @Test
    void checkHashIdempotency() {
        FeatureFlagsWithHash featureFlagsWithHash = featureFlagService.getFeatureFlagsWithHash();
        assertThat(featureFlagsWithHash.getFeatureFlagsHash())
                .isEqualTo(featureFlagService.getFeatureFlagsWithHash()
                        .getFeatureFlagsHash()
                );
    }

    @SneakyThrows
    @Test
    void checkHashChangedAfterMapChanged() {
        FeatureFlagsWithHash flagsHash = featureFlagService.getFeatureFlagsWithHash();
        HashMap<String, FeatureFlag> map = new HashMap<>(featureFlags.getFeatureMap());
        map.remove(TAGGED_FEATURE_NAME);
        when(extractor.getFeatureFlags()).thenReturn(new FeatureFlags(map));
        featureFlagsHolder.refreshFeatureFlags();
        assertThat(flagsHash.getFeatureFlagsHash())
                .isNotEqualTo(featureFlagService.getFeatureFlagsWithHash()
                        .getFeatureFlagsHash()
                );
    }

    @SneakyThrows
    @Test
    void checkHashIgnoresOrder() {
        LinkedHashMap<String, FeatureFlag> map = new LinkedHashMap<>();
        map.put(UNTAGGED_FEATURE_NAME, TestFeatureFlagsProvider.provideFeatureFlagWithoutTag());
        map.put(TAGGED_FEATURE_NAME, TestFeatureFlagsProvider.provideFeatureFlagWithTag());
        when(extractor.getFeatureFlags()).thenReturn(new FeatureFlags(map));
        featureFlagsHolder.refreshFeatureFlags();
        FeatureFlagsWithHash flagsHash = featureFlagService.getFeatureFlagsWithHash();

        map.remove(UNTAGGED_FEATURE_NAME);
        map.put(UNTAGGED_FEATURE_NAME, TestFeatureFlagsProvider.provideFeatureFlagWithoutTag());
        when(extractor.getFeatureFlags()).thenReturn(new FeatureFlags(map));
        featureFlagsHolder.refreshFeatureFlags();
        assertThat(flagsHash.getFeatureFlagsHash())
                .isEqualTo(featureFlagService.getFeatureFlagsWithHash()
                        .getFeatureFlagsHash()
                );
    }
}
