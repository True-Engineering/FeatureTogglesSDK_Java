package ru.trueengineering.feature.flag.starter.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlags;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для извлечения ранее вычисленных фичефлагов из запроса
 * @see EvaluatedFeatureFlags
 */
@Component
@RequiredArgsConstructor
public class EvaluatedFeatureFlagExtractorFilter extends OncePerRequestFilter {
    private final EvaluatedFeatureFlagHeaderHandler contextHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        contextHandler.deserializeContext(request);
        filterChain.doFilter(request, response);
    }
}
