package ru.trueengineering.feature.flag.starter.strategy;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;

/**
 * Стратегия на основе контекста пользователя.
 * Открывает фичу, если данные пользователя соответствуют заданным в стратегии.
 */
@Service
public class RequestContextFlippingStrategy implements FeatureFlagStrategy {

    private static final String PARAMETERS_SEPARATOR = ";";

    @Override
    public String getClassName() {
        return "ru.trueengineering.feature.flag.portal.strategy.RequestContextFlippingStrategy";
    }

    @Override
    public boolean evaluate(FeatureFlagContext executionContext, Map<String, String> initParams) {

        if (initParams == null || initParams.isEmpty()) {
            return true;
        }

        Map<String, Set<String>> initParamsSet = initParams.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        entry -> splitToSet(entry.getValue()),
                        (t1, t2) -> t1));

        Map<String, String> currentContextParams = Optional.ofNullable(executionContext)
                .map(FeatureFlagContext::getParams)
                .orElse(emptyMap());

        return initParamsSet.entrySet().stream()
                .allMatch(entry -> CollectionUtils.isEmpty(entry.getValue()) ||
                        entry.getValue().contains(currentContextParams.get(entry.getKey())));
    }

    private Set<String> splitToSet(String params) {
        if (StringUtils.isEmpty(params)) {
            return emptySet();
        }
        return Arrays.stream(
                        StringUtils.split(params, PARAMETERS_SEPARATOR))
                .collect(Collectors.toSet());
    }
}
