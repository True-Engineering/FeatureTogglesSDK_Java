package ru.trueengineering.feature.flag.starter.provider;

import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagStrategyDetails;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.util.Collections;
import java.util.Map;

import static ru.trueengineering.feature.flag.starter.provider.MapUtils.entryOf;
import static ru.trueengineering.feature.flag.starter.provider.MapUtils.mapOf;

public class TestFeatureFlagsProvider {

    public static final boolean TEST_FEATURE_ENABLED = true;
    public static final String TAGGED_FEATURE_NAME = "taggedTestFeature";
    public static final String UNTAGGED_FEATURE_NAME = "untaggedTestFeature";
    public static final String FEATURE_WITH_STRATEGY_NAME = "withStrategyFeature";
    public static final String TEST_FEATURE_DESCRIPTION = "testFeatureDescription";
    public static final String TEST_PROPERTY_KEY = "property-key";
    public static final String TEST_PROPERTY_VALUE = "property-value";
    public static final String STRATEGY_PARAM_KEY_ONE = "param_one";
    public static final String STRATEGY_PARAM_VALUE_ONE = "param_value_one";
    public static final String STRATEGY_PARAM_KEY_TWO = "param_two";
    public static final String STRATEGY_PARAM_VALUE_TWO = "param_value_two";
    public static final String STRATEGY_CLASS_NAME = "StrategyClassName";

    public static final String WEB_TAG = "WEB";
    public static final String OTHER_TAG = "OTHER";
    public static final String TAG_KEY = "tag";
    public static final String TAG_VALUE = String.join(", ", WEB_TAG, OTHER_TAG);

    public static FeatureFlags provideFeatureFlags() {
        return new FeatureFlags(
                mapOf(
                        entryOf(FEATURE_WITH_STRATEGY_NAME, provideFeatureFlagWithStrategy()),
                        entryOf(UNTAGGED_FEATURE_NAME, provideFeatureFlagWithoutTag()),
                        entryOf(TAGGED_FEATURE_NAME, provideFeatureFlagWithTag())
                )
        );
    }

    public static FeatureFlag provideFeatureFlagWithTag() {
        return provideFeatureFlag(
                TAGGED_FEATURE_NAME,
                mapOf(entryOf(TAG_KEY, TAG_VALUE))
        );
    }

    public static FeatureFlag provideFeatureFlagWithStrategy() {
        FeatureFlagStrategyDetails flippingStrategy = new FeatureFlagStrategyDetails(
                STRATEGY_CLASS_NAME,
                getParams());

        return new FeatureFlag(
                FEATURE_WITH_STRATEGY_NAME,
                TEST_FEATURE_ENABLED,
                TEST_FEATURE_DESCRIPTION,
                null,
                Collections.emptyList(),
                Collections.emptyMap(),
                flippingStrategy
        );
    }

    public static Map<String, String> getParams() {
        return mapOf(entryOf(STRATEGY_PARAM_KEY_ONE, STRATEGY_PARAM_VALUE_ONE),
                entryOf(STRATEGY_PARAM_KEY_TWO, STRATEGY_PARAM_VALUE_TWO));
    }

    public static FeatureFlag provideFeatureFlagWithoutTag() {
        return provideFeatureFlag(
                UNTAGGED_FEATURE_NAME,
                mapOf(entryOf(TEST_PROPERTY_KEY, TEST_PROPERTY_VALUE))
        );
    }

    private static FeatureFlag provideFeatureFlag(
            String name,
            Map<String, String> properties) {
        return new FeatureFlag(
                name,
                TEST_FEATURE_ENABLED,
                TEST_FEATURE_DESCRIPTION,
                null,
                Collections.emptyList(),
                properties,
                null
        );
    }
}
