package ru.trueengineering.feature.flag.starter.propagation.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;

@RequiredArgsConstructor
@Slf4j
public class FeatureFlagContextFeignInterceptor implements RequestInterceptor {
    private final EvaluatedFeatureFlagHeaderHandler contextHandler;

    @Override
    public void apply(RequestTemplate template) {
        contextHandler.serializeContext(template::header);
    }
}
