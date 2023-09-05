package ru.trueengineering.feature.flag.starter.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.entryOf;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.mapOf;

class ReleaseDateFlipStrategyTest {

    private static final LocalDateTime YESTERDAY = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);
    private static final LocalDateTime TOMORROW = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);

    private static final String DATE_PATTERN = "yyyy-MM-dd-HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final String RELEASE_DATE_KEY = "releaseDate";

    private final ReleaseDateFlipStrategy uut = new ReleaseDateFlipStrategy();

    @Test
    void evaluateNullInitParams() {
        FeatureFlagContext featureFlagContext = new FeatureFlagContext(emptyMap());
        boolean result = uut.evaluate(featureFlagContext, null);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void evaluate(Map<String, String> initParams, boolean expectedResult) {

        FeatureFlagContext featureFlagContext = new FeatureFlagContext(emptyMap());
        boolean result = uut.evaluate(featureFlagContext, initParams);
        assertThat(result).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(mapOf(entryOf(RELEASE_DATE_KEY, YESTERDAY.format(DATE_TIME_FORMATTER))), true),
                Arguments.of(mapOf(entryOf(RELEASE_DATE_KEY, TOMORROW.format(DATE_TIME_FORMATTER))), false),
                Arguments.of(emptyMap(), true)
        );
    }

}