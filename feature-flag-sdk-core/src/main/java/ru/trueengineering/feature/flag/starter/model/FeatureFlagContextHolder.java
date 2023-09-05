package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeatureFlagContextHolder {

    private final FeatureFlagContext featureFlagContext;
}
