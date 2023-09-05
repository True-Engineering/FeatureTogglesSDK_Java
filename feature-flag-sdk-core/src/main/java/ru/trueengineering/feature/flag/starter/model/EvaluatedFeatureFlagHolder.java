package ru.trueengineering.feature.flag.starter.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Хранит контекст состояния фичефлагов.
 * @see EvaluatedFeatureFlags
 */
@Getter
@Setter
public class EvaluatedFeatureFlagHolder {
    private EvaluatedFeatureFlags context;
}
