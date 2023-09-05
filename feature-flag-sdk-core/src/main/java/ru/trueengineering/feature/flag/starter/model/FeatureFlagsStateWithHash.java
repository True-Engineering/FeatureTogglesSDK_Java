package ru.trueengineering.feature.flag.starter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FeatureFlagsStateWithHash {

    private Map<String, Boolean> values;

    private String hash;

}
