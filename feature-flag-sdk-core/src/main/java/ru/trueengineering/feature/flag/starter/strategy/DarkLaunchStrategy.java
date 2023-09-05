package ru.trueengineering.feature.flag.starter.strategy;

import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Map;

/**
 * Стратегия на основе процента вероятности.
 * Позволяет открыть фичу заданному проценту запросов.
 */
@Service
public class DarkLaunchStrategy implements FeatureFlagStrategy {

    private static final String WEIGHT_KEY = "weight";

    @Override
    public String getClassName() {
        return "org.ff4j.strategy.DarkLaunchStrategy";
    }

    @Override
    public boolean evaluate(FeatureFlagContext executionContext, Map<String, String> initParams) {

        if (initParams == null || initParams.isEmpty()) {
            return true;
        }

        String tresholdString = initParams.get(WEIGHT_KEY);

        if (tresholdString == null) {
            return true;
        }

        return Math.random() <= Double.parseDouble(tresholdString);
    }
}
