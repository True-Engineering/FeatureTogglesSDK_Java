package ru.trueengineering.feature.flag.starter.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlagHolder;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContextHolder;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsStateWithHash;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategy;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategyProvider;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.trueengineering.feature.flag.starter.provider.MapFeatureFlagStateProviderTest.INCOMPLETE_TAG;
import static ru.trueengineering.feature.flag.starter.provider.MapFeatureFlagStateProviderTest.NOT_EXISTING_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.MapFeatureFlagStateProviderTest.NOT_EXISTING_TAG;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.TAGGED_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.TEST_FEATURE_ENABLED;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.UNTAGGED_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.WEB_TAG;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.provideFeatureFlags;

public class EvaluatedFeatureFlagStateProviderTest {

    private final FeatureFlags featureFlags = provideFeatureFlags();

    private EvaluatedFeatureFlagStateProvider provider;

    private final Map<String, Boolean> contextMap = new HashMap<>();

    @Mock
    private FeatureFlagsHolder featureFlagsHolder;
    @Mock
    private FeatureFlagContextHolder featureFlagHolder;
    @Mock
    private FeatureFlagStrategyProvider featureFlagStrategyProvider;
    @Mock
    private FeatureFlagStrategy featureFlagStrategy;
    @Mock
    private FeatureFlagScanNamesProperties featureFlagScanNamesProperties;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        EvaluatedFeatureFlagHolder contextHolder = new EvaluatedFeatureFlagHolder();
        contextHolder.setContext(new EvaluatedFeatureFlags(contextMap));
        provider = new EvaluatedFeatureFlagStateProvider(
                new MapFeatureFlagStateProvider(featureFlagsHolder, featureFlagHolder,
                        featureFlagStrategyProvider, featureFlagScanNamesProperties),
                contextHolder
        );
        when(featureFlagStrategyProvider.getStrategy(any())).thenReturn(featureFlagStrategy);
        when(featureFlagsHolder.getFeatureFlags()).thenReturn(featureFlags);

    }

    @Test
    void checkFeatureExist() {
        contextMap.put(UNTAGGED_FEATURE_NAME, false);

        assertThat(provider.check(TAGGED_FEATURE_NAME)).isTrue();
        assertThat(provider.check(UNTAGGED_FEATURE_NAME)).isFalse();
        assertThat(contextMap)
                .containsEntry(TAGGED_FEATURE_NAME, true)
                .containsEntry(UNTAGGED_FEATURE_NAME, false);
    }

    @Test
    void checkFeatureNotExist() {
        assertThat(provider.check(NOT_EXISTING_FEATURE_NAME)).isFalse();
    }

    @Test
    void checkNullFeature() {
        assertThat(provider.check(null)).isFalse();
    }

    @Test
    void checkGetByExistingTag() {
        FeatureFlag expectedFeature = TestFeatureFlagsProvider.provideFeatureFlagWithTag();
        assertThat(provider.getByTag(WEB_TAG))
                .containsEntry(
                        expectedFeature.getUid(),
                        expectedFeature.isEnable()
                )
                .size()
                .isEqualTo(1);
        assertThat(contextMap)
                .containsEntry(TAGGED_FEATURE_NAME, TEST_FEATURE_ENABLED);
    }

    @Test
    void checkGetByIncompleteTag() {
        assertThat(provider.getByTag(INCOMPLETE_TAG)).isEmpty();
        assertThat(contextMap).isEmpty();
    }

    @Test
    void checkGetByNonExistingTag() {
        assertThat(provider.getByTag(NOT_EXISTING_TAG)).isEmpty();
        assertThat(contextMap).isEmpty();
    }

    @Test
    void checkGetByNullTag() {
        assertThat(provider.getByTag(null)).isEmpty();
        assertThat(contextMap).isEmpty();
    }

    @Test
    void checkGetByExistingTagWithHash() {
        FeatureFlag expectedFeature = TestFeatureFlagsProvider.provideFeatureFlagWithTag();
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(WEB_TAG);
        assertThat(ffState.getValues())
                .containsEntry(
                        expectedFeature.getUid(),
                        expectedFeature.isEnable()
                )
                .size()
                .isEqualTo(1);
        assertThat(ffState.getHash()).isEqualTo(featureFlags.getFlagsHash());
        assertThat(contextMap)
                .containsEntry(TAGGED_FEATURE_NAME, TEST_FEATURE_ENABLED);

    }

    @Test
    void checkGetByIncompleteTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(INCOMPLETE_TAG);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
        assertThat(contextMap).isEmpty();
    }

    @Test
    void checkGetByNonExistingTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(NOT_EXISTING_TAG);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
        assertThat(contextMap).isEmpty();
    }

    @Test
    void checkGetByNullTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(null);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
        assertThat(contextMap).isEmpty();
    }
}
