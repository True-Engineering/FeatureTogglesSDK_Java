package ru.trueengineering.feature.flag.starter.strategy;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class FeatureFlagStrategyProvider {

    private final Map<String, FeatureFlagStrategy> featureFlagStrategyMap;

    public FeatureFlagStrategy getStrategy(String strategyName) {
        return featureFlagStrategyMap.get(strategyName);
    }
}
