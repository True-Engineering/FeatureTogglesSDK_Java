package ru.trueengineering.feature.flag.starter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureFlag implements Serializable {

    @NotNull
    private String uid;

    private boolean enable;

    private String description;
    private String group;
    private List<String> permissions;
    private Map<String, String> customProperties;
    private FeatureFlagStrategyDetails flippingStrategy;
}
