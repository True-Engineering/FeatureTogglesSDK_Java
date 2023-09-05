package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategy;

import java.util.Map;

/**
 * Содержит информацию о стратегии фичефлага.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeatureFlagStrategyDetails {

    /**
     * Имя класса стратегии. По этому имени выбираем имплементацию {@link FeatureFlagStrategy}
     */
    private String className;

    /**
     * Параметры стратегии, задаваемые пользователем.
     * Стратегия проверяет соответствие контекста фичефлага этим параметрам.
     */
    private Map<String, String> initParams;
}
