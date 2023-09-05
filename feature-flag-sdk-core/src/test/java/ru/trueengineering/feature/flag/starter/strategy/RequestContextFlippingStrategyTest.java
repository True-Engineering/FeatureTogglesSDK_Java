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

class RequestContextFlippingStrategyTest {

    public static final String STRATEGY_PARAM_KEY_ONE = "param_one";
    public static final String STRATEGY_PARAM_VALUE_ONE = "param_value_one";
    public static final String STRATEGY_PARAM_KEY_TWO = "param_two";
    public static final String STRATEGY_PARAM_VALUE_TWO = "param_value_two";
    public static final Map<String, String> INIT_PARAMS = mapOf(
            entryOf(STRATEGY_PARAM_KEY_ONE, STRATEGY_PARAM_VALUE_ONE),
            entryOf(STRATEGY_PARAM_KEY_TWO, STRATEGY_PARAM_VALUE_TWO));

    private final RequestContextFlippingStrategy uut = new RequestContextFlippingStrategy();

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
                Arguments.of(mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, STRATEGY_PARAM_VALUE_ONE)), false),
                Arguments.of(mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, STRATEGY_PARAM_VALUE_TWO),
                        entryOf(STRATEGY_PARAM_KEY_TWO, STRATEGY_PARAM_VALUE_ONE)), false),
                Arguments.of(mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, STRATEGY_PARAM_VALUE_ONE),
                        entryOf(STRATEGY_PARAM_KEY_TWO, STRATEGY_PARAM_VALUE_ONE)), false)
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsParametersList")
    void evaluateParameterList(Map<String, String> initParams,
                               Map<String, String> featureContext,
                               boolean expectedResult) {

        FeatureFlagContext featureFlagContext = new FeatureFlagContext(featureContext);
        boolean result = uut.evaluate(featureFlagContext, initParams);
        assertThat(result).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> argumentsParametersList() {
        Map<String, String> twoValueMap = mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value1;value2"));
        Map<String, String> zeroValueMap = mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, ""));
        Map<String, String> nullValueMap = mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, null));
        return Stream.of(
                Arguments.of(twoValueMap, mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value1")), true),
                Arguments.of(twoValueMap, mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value1")), true),
                Arguments.of(twoValueMap, mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value3")), false),
                Arguments.of(twoValueMap, emptyMap(), false),
                Arguments.of(zeroValueMap, emptyMap(), true),
                Arguments.of(zeroValueMap, mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value2")), true),
                Arguments.of(nullValueMap, emptyMap(), true),
                Arguments.of(nullValueMap, mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, "value2")), true)
        );
    }
}