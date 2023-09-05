package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagContextProvider;

import java.util.Map;

/**
 * Контекст проверки состояния фичефлага с учетом его стратегии.
 * Содержит текущие параметры (запроса, приложения, клиента и т.п.),
 * которые можно добавить с помощью реализации интерфейса
 * {@link FeatureFlagContextProvider}.
 * Эти параметры будут учитываться стратегией проверки фичефлага.
 */
@Data
@AllArgsConstructor
public class FeatureFlagContext {

    private Map<String, String> params;
}
