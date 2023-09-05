package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author m.yastrebov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class FeaturesHolder {

    private Management featureManagement;

    public FeaturesHolder(List<FeatureFlag> features) {
        this(new Management(features));
    }

    public FeatureFlags convertToFlags() {
        return new FeatureFlags(
                Optional.ofNullable(this.getFeatureManagement())
                        .map(FeaturesHolder.Management::getFeatures).orElse(new ArrayList<>())
                        .stream()
                        .collect(Collectors.toConcurrentMap(FeatureFlag::getUid, it -> it,
                                this::logNotUniqueFeatureFlag))
        );
    }

    private FeatureFlag logNotUniqueFeatureFlag(FeatureFlag o1, FeatureFlag o2) {
        log.error("Not unique feature flag id {}. {} and {}", o1.getUid(), o1, o2);
        return o1;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Management {

        private List<FeatureFlag> features;

    }
}
