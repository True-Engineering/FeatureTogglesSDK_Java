package ru.trueengineering.feature.flag.starter.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlagHolder;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlags;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsStateWithHash;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.trueengineering.feature.flag.starter.util.RequestScopeUtils.runInRequestScopeOr;

/**
 * Класс проверяет состояние фичефлага с учетом ранее проверенных флагов.
 */
@RequiredArgsConstructor
public class EvaluatedFeatureFlagStateProvider implements FeatureFlagStateProvider {
    private final FeatureFlagStateProvider provider;

    private final EvaluatedFeatureFlagHolder contextHolder;

    @Override
    public boolean check(@NonNull String featureName) {
        return runInRequestScopeOr(
                () -> checkWithContext(featureName),
                () -> provider.check(featureName)
        );
    }

    @Override
    public Map<String, Boolean> getByTag(String tag) {
        return runInRequestScopeOr(
                () -> getWithContext(provider.getByTag(tag)),
                () -> provider.getByTag(tag)
        );
    }

    @Override
    public FeatureFlagsStateWithHash getWithHashByTag(String tag) {
        return runInRequestScopeOr(
                () -> {
                    FeatureFlagsStateWithHash ffs = provider.getWithHashByTag(tag);
                    return new FeatureFlagsStateWithHash(getWithContext(ffs.getValues()), ffs.getHash());
                },
                () -> provider.getWithHashByTag(tag)
        );
    }

    private boolean checkWithContext(String featureName) {
        EvaluatedFeatureFlags context = contextHolder.getContext();
        return context.getOrComputeIfAbsent(featureName, provider::check);
    }

    private Map<String, Boolean> getWithContext(Map<String, Boolean> allFeatureFlags) {
        EvaluatedFeatureFlags context = contextHolder.getContext();
        return allFeatureFlags
                .entrySet()
                .stream()
                .peek(entry -> entry.setValue(
                        context.getOrPutIfAbsent(
                                entry.getKey(),
                                entry.getValue()
                        )
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

}
