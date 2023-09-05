package ru.trueengineering.feature.flag.starter.propagation.resttemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;

import java.io.IOException;


@RequiredArgsConstructor
public class FeatureFlagContextRequestInterceptor implements ClientHttpRequestInterceptor {
    private final EvaluatedFeatureFlagHeaderHandler contextHandler;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        contextHandler.serializeContext(
                request.getHeaders()::set
        );
        return execution.execute(request, body);
    }
}
