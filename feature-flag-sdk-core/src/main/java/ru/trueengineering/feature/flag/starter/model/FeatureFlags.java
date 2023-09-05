package ru.trueengineering.feature.flag.starter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.trueengineering.feature.flag.starter.util.HashUtils;

import java.util.Collections;
import java.util.Map;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class FeatureFlags {
    private Map<String, FeatureFlag> featureMap;

    private String flagsHash;

    public FeatureFlags(Map<String, FeatureFlag> featureMap) {
        this.featureMap = Collections.unmodifiableMap(featureMap);
        this.flagsHash = HashUtils.getHash(this.featureMap.values());
    }

}
