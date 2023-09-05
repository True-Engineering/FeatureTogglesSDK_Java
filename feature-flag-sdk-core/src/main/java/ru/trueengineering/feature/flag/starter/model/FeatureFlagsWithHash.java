package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeatureFlagsWithHash {
    private Map<String, FeatureFlag> featureFlags;

    private String featureFlagsHash;
}
