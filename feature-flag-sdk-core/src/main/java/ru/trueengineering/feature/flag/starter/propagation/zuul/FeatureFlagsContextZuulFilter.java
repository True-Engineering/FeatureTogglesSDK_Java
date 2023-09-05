package ru.trueengineering.feature.flag.starter.propagation.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;


@RequiredArgsConstructor
@Slf4j
public class FeatureFlagsContextZuulFilter extends ZuulFilter {
    private final EvaluatedFeatureFlagHeaderHandler contextHandler;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        contextHandler.serializeContext(
                RequestContext.getCurrentContext()::addZuulRequestHeader
        );
        return null;
    }
}
