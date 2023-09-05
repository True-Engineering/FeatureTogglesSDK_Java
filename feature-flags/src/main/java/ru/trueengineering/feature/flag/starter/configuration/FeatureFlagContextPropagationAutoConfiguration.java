package ru.trueengineering.feature.flag.starter.configuration;

import com.netflix.zuul.ZuulFilter;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;
import ru.trueengineering.feature.flag.starter.propagation.feign.FeatureFlagContextFeignInterceptor;
import ru.trueengineering.feature.flag.starter.propagation.resttemplate.FeatureFlagContextRequestInterceptor;
import ru.trueengineering.feature.flag.starter.propagation.zuul.FeatureFlagsContextZuulFilter;

@Configuration
@ConditionalOnProperty(value = "feature.flags.http.headers.forward.enabled", havingValue = "true",
        matchIfMissing = true)
public class FeatureFlagContextPropagationAutoConfiguration {

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public ClientHttpRequestInterceptor jwtTokenRequestInterceptor(
            RestTemplate restTemplate,
            EvaluatedFeatureFlagHeaderHandler contextHandler) {
        final FeatureFlagContextRequestInterceptor requestInterceptor
                = new FeatureFlagContextRequestInterceptor(contextHandler);
        restTemplate.getInterceptors().add(requestInterceptor);
        return requestInterceptor;
    }

    @ConditionalOnClass(ZuulFilter.class)
    static class ZuulFilterConfiguration {
        @Bean
        public ZuulFilter authTokenZuulFilter(EvaluatedFeatureFlagHeaderHandler contextHandler) {
            return new FeatureFlagsContextZuulFilter(contextHandler);
        }
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
    public RequestInterceptor jwtTokenFeignRequestInterceptor(EvaluatedFeatureFlagHeaderHandler contextHandler) {
        return new FeatureFlagContextFeignInterceptor(contextHandler);
    }
}
