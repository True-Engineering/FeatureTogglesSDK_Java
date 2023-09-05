package ru.trueengineering.feature.flag.starter.strategy;

import org.junit.jupiter.api.Test;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.entryOf;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.mapOf;

class DarkLaunchStrategyTest {

    private static final String APP_VERSION_KEY = "appVersion";
    private static final String APP_VERSION_VALUE = "v1";
    public static final Map<String, String> INIT_PARAMS = mapOf(
            entryOf(APP_VERSION_KEY, APP_VERSION_VALUE));

    private final DarkLaunchStrategy uut = new DarkLaunchStrategy();

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

}