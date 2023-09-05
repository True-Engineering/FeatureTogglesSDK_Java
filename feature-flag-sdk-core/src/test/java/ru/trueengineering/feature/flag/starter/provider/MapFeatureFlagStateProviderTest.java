package ru.trueengineering.feature.flag.starter.provider;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContextHolder;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsStateWithHash;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategy;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategyProvider;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.FEATURE_WITH_STRATEGY_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.TAGGED_FEATURE_NAME;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.WEB_TAG;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.provideFeatureFlags;

class MapFeatureFlagStateProviderTest {

    private final Logger log = (Logger) LoggerFactory.getLogger(MapFeatureFlagStateProvider.class);

    public static final String NOT_EXISTING_FEATURE_NAME = "test.feature.not.exist";
    public static final String NOT_EXISTING_TAG = "tag.not.exist";
    public static final String INCOMPLETE_TAG = WEB_TAG.substring(0, 2);

    private final FeatureFlags featureFlags = provideFeatureFlags();

    private MapFeatureFlagStateProvider provider;

    @Mock
    private FeatureFlagsHolder featureFlagsHolder;
    @Mock
    private FeatureFlagContextHolder featureFlagContextHolder;
    @Mock
    private FeatureFlagStrategyProvider featureFlagStrategyProvider;
    @Mock
    private FeatureFlagStrategy featureFlagStrategy;
    @Mock
    private FeatureFlagScanNamesProperties scanNamesProperties;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        provider = new MapFeatureFlagStateProvider(
                featureFlagsHolder, featureFlagContextHolder, featureFlagStrategyProvider, scanNamesProperties);
        when(scanNamesProperties.isEnabled()).thenReturn(true);
        when(featureFlagStrategyProvider.getStrategy(any())).thenReturn(featureFlagStrategy);
        when(featureFlagsHolder.getFeatureFlags()).thenReturn(featureFlags);
        when(featureFlagsHolder.hasFeatureFlag(TAGGED_FEATURE_NAME)).thenReturn(true);
        when(featureFlagsHolder.hasFeatureFlag(NOT_EXISTING_FEATURE_NAME)).thenReturn(false);
    }

    @Test
    void checkFeatureExist() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        assertThat(provider.check(TAGGED_FEATURE_NAME)).isTrue();

        assertThat(listAppender.list.size()).isEqualTo(1);
        assertThat(listAppender.list.get(0).getLevel().toString()).isEqualTo("DEBUG");
        assertThat(listAppender.list.get(0).getFormattedMessage()).isEqualTo(
                "State of feature taggedTestFeature = enabled");
    }

    @Test
    void checkFeatureNotExist() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        assertThat(provider.check(NOT_EXISTING_FEATURE_NAME)).isFalse();

        assertThat(listAppender.list.size()).isEqualTo(2);
        assertThat(listAppender.list.get(0).getLevel().toString()).isEqualTo("WARN");
        assertThat(listAppender.list.get(0).getFormattedMessage()).isEqualTo(
                "Feature flag test.feature.not.exist is not found in the holder");
        assertThat(listAppender.list.get(1).getLevel().toString()).isEqualTo("DEBUG");
        assertThat(listAppender.list.get(1).getFormattedMessage()).isEqualTo(
                "State of feature test.feature.not.exist = disabled");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
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
    }

    @Test
    void checkGetByIncompleteTag() {
        assertThat(provider.getByTag(INCOMPLETE_TAG)).isEmpty();
    }

    @Test
    void checkGetByNonExistingTag() {
        assertThat(provider.getByTag(NOT_EXISTING_TAG)).isEmpty();
    }

    @Test
    void checkGetByNullTag() {
        assertThat(provider.getByTag(null)).isEmpty();
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
    }

    @Test
    void checkGetByIncompleteTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(INCOMPLETE_TAG);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
    }

    @Test
    void checkGetByNonExistingTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(NOT_EXISTING_TAG);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
    }

    @Test
    void checkGetByNullTagWithHash() {
        FeatureFlagsStateWithHash ffState = provider.getWithHashByTag(null);
        assertThat(ffState.getValues()).isEmpty();
        assertThat(ffState.getHash()).isEmpty();
    }

    @Test
    void checkWithStrategyTrue() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(
                TestFeatureFlagsProvider.getParams());
        when(featureFlagContextHolder.getFeatureFlagContext()).thenReturn(featureFlagContext);
        when(featureFlagStrategy.evaluate(featureFlagContext,
                TestFeatureFlagsProvider.getParams())).thenReturn(true);
        assertThat(provider.check(FEATURE_WITH_STRATEGY_NAME)).isTrue();
    }

    @Test
    void checkWithStrategyFalse() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(
                TestFeatureFlagsProvider.getParams());
        when(featureFlagContextHolder.getFeatureFlagContext()).thenReturn(featureFlagContext);
        when(featureFlagStrategy.evaluate(featureFlagContext,
                TestFeatureFlagsProvider.getParams())).thenReturn(false);
        assertThat(provider.check(FEATURE_WITH_STRATEGY_NAME)).isFalse();
    }

    @Test
    void checkWithNullStrategy() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(
                TestFeatureFlagsProvider.getParams());
        when(featureFlagContextHolder.getFeatureFlagContext()).thenReturn(featureFlagContext);
        when(featureFlagStrategyProvider.getStrategy(any())).thenReturn(null);
        assertThat(provider.check(FEATURE_WITH_STRATEGY_NAME)).isTrue();
    }
}
