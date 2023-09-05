package ru.trueengineering.feature.flag.starter.strategy;

import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Map;
import java.util.Optional;

/**
 * Стратегия версии приложения.
 * Открывает фичу приложению с указанной версией.
 * Если в параметрах стратегии не указана версия, считаем, что все версии подходят.
 */
@Service
public class AppVersionStrategy implements FeatureFlagStrategy {

    private static final String APP_VERSION_STRATEGY_CLASS_NAME =
            "ru.trueengineering.feature.flag.portal.strategy.AppVersionStrategy";

    private static final String APP_VERSION_KEY = "appVersion";

    @Override
    public String getClassName() {
        return APP_VERSION_STRATEGY_CLASS_NAME;
    }

    @Override
    public boolean evaluate(FeatureFlagContext executionContext, Map<String, String> initParams) {
        if (initParams == null || initParams.isEmpty()) {
            return true;
        }
        String appVersion = initParams.get(APP_VERSION_KEY);

        // Если в параметрах стратегии не указана версия, считаем, что все версии подходят
        if (appVersion == null) {
            return true;
        }

        return Optional.ofNullable(executionContext)
                .map(FeatureFlagContext::getParams)
                .map(it -> it.get(APP_VERSION_KEY))
                .map(appVersion::equals)
                .orElse(false);
    }
}
