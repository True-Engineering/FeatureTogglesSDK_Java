package ru.trueengineering.feature.flag.starter.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.entryOf;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.mapOf;

class AppVersionStrategyTest {

    private static final String APP_VERSION_KEY = "appVersion";
    private static final String APP_VERSION_VALUE = "v1";
    private static final String WRONG_APP_VERSION_VALUE = "v2";
    public static final Map<String, String> INIT_PARAMS = mapOf(
            entryOf(APP_VERSION_KEY, APP_VERSION_VALUE));
    private static final String FEATURE_NAME = "FEATURE_NAME";

    private final AppVersionStrategy uut = new AppVersionStrategy();

    @Test
    void evaluateEmptyInitParams() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(INIT_PARAMS);
        boolean result = uut.evaluate(featureFlagContext, emptyMap());
        assertThat(result).isTrue();
    }

    @Test
    void evaluateNullInitParams() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(INIT_PARAMS);
        boolean result = uut.evaluate(featureFlagContext, null);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void evaluate(Map<String, String> featureContext, boolean expectedResult) {

        FeatureFlagContext featureFlagContext = new FeatureFlagContext(featureContext);
        boolean result = uut.evaluate(featureFlagContext, INIT_PARAMS);
        assertThat(result).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(INIT_PARAMS, true),
                Arguments.of(emptyMap(), false),
                Arguments.of(mapOf(entryOf(APP_VERSION_KEY, WRONG_APP_VERSION_VALUE)), false)
        );
    }
}