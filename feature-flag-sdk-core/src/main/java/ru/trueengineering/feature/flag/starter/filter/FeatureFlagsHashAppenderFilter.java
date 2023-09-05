package ru.trueengineering.feature.flag.starter.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagsSdkProperties;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@ConditionalOnExpression("${feature.flag.hash.header.enabled:true} || ${feature.flag.controller.enabled:true}")
public class FeatureFlagsHashAppenderFilter extends OncePerRequestFilter {
    private final FeatureFlagsSdkProperties properties;

    private final FeatureFlagsHolder featureFlagsHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tag = request.getHeader(properties.getTagHeader());
        if (tag != null) {
            response.setHeader(properties.getTagHeader(), tag);
        }

        response.setHeader(
                properties.getHashHeader(),
                tag == null
                        ? featureFlagsHolder.getFeatureFlags().getFlagsHash()
                        : featureFlagsHolder.getFeatureFlagsByTag(tag).getFlagsHash()
        );

        filterChain.doFilter(request, response);
    }
}
