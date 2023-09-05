package ru.trueengineering.feature.flag.starter.strategy;

import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Map;

public interface FeatureFlagStrategy {

    /**
     * Возвращает полное имя класса стратегии в хранилище фичефлагов
     */
    String getClassName();

    /**
     * Проверяет соответствие контекста фичефлага заданным параметрам
     *
     * @param executionContext  текущий контекст фичефлага
     * @param initParams        параметры стратегии, заданные пользователем

     */
    boolean evaluate(FeatureFlagContext executionContext, Map<String, String> initParams);
}
