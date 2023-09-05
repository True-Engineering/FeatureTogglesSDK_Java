package ru.trueengineering.feature.flag.starter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Содержит значения фичефлагов, которые уже были определены в рамках текущего запроса.
 * Используется для сохранения консистентности состояния фичефлага в нескольких микросервисах в рамках одного запроса.
 * Состояние каждого запрошенного фичефлага добавляется в данную модель, после чего последующие микросервисы,
 * обрабатывающие запрос, не вычислят значение повторно, а получают значение фичефлага из данной модели.
 * Позволяет избежать ситуации, когда состояние фичефлага изменяется во время выполнения запроса и разные микросервисы,
 * обрабатывающие один запрос видят разное состояние одного фичефлага.
 */
@Getter
@Setter
@NoArgsConstructor
public class EvaluatedFeatureFlags {
    private Map<String, Boolean> featureMap = new ConcurrentHashMap<>();

    public EvaluatedFeatureFlags(Map<String, Boolean> featureMap) {
        this.featureMap = featureMap;
    }

    public boolean getOrComputeIfAbsent(String featureName, Function<String, Boolean> mappingFunction) {
        featureMap.computeIfAbsent(featureName, mappingFunction);
        return featureMap.get(featureName);
    }

    public boolean getOrPutIfAbsent(String featureName, boolean value) {
        featureMap.putIfAbsent(featureName, value);
        return featureMap.get(featureName);
    }
}
