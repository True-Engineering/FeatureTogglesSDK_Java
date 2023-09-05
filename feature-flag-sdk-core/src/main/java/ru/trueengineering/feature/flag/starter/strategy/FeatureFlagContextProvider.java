package ru.trueengineering.feature.flag.starter.strategy;

import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

/**
 * Провайдер, предоставляющий контекст фичефлага, для проверки состояния фичефлага с помощью стратегии. <br/>
 *
 * Если пользователь не создал свою реализацию,
 * будет использован провайдер по умолчанию, который возвращает пустой контекст.
 *
 * @see FeatureFlagContext
 */
@FunctionalInterface
public interface FeatureFlagContextProvider {

    FeatureFlagContext getFlippingExecutionContext();
}
