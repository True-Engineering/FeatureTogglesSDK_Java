package ru.trueengineering.feature.flag.starter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsWithHash;
import ru.trueengineering.feature.flag.starter.service.FeatureFlagService;

@RestController
@ConditionalOnProperty(prefix = "feature.flag.controller", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class FeatureFlagsController {
    private final FeatureFlagService featureFlagService;

    @GetMapping("/api/features")
    public FeatureFlagsWithHash getFeatureFlagsWithHash(@RequestParam (required = false) String tag) {
        return tag == null
                ? featureFlagService.getFeatureFlagsWithHash()
                : featureFlagService.getFeatureFlagsWithHashByTag(tag);
    }
}
