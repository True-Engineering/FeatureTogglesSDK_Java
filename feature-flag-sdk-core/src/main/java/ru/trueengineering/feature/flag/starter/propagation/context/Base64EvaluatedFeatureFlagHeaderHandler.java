package ru.trueengineering.feature.flag.starter.propagation.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlagHolder;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlags;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Slf4j
public class Base64EvaluatedFeatureFlagHeaderHandler implements EvaluatedFeatureFlagHeaderHandler {
    public static final String FF_CONTEXT_HEADER = "FF-Context";

    private final ObjectMapper objectMapper;

    private final EvaluatedFeatureFlagHolder contextHolder;

    @Override
    public void serializeContext(ContextInjector contextInjector) {
        try {
            contextInjector.inject(
                    FF_CONTEXT_HEADER,
                    encodeContext()
            );
        } catch (IOException exc) {
            log.error("Error injecting feature flag context into request: {}", exc.getLocalizedMessage());
        }
    }

    @Override
    public void deserializeContext(HttpServletRequest request) {
        try {
            String contextHeaderValue = request.getHeader(FF_CONTEXT_HEADER);
            contextHolder.setContext(
                    isNull(contextHeaderValue)
                            ? new EvaluatedFeatureFlags()
                            : decodeContext(contextHeaderValue)
            );
        } catch (IOException exc) {
            log.error("Error extracting feature flag context from request: {}", exc.getLocalizedMessage());
        }
    }

    private EvaluatedFeatureFlags decodeContext(String encodedContext) throws IOException {
        byte[] decodedJson = Base64.getDecoder().decode(encodedContext.getBytes(StandardCharsets.UTF_8));
        return objectMapper.readValue(decodedJson, new TypeReference<EvaluatedFeatureFlags>() {});
    }

    private String encodeContext() throws JsonProcessingException {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(contextHolder.getContext());
            return new String(
                    Base64.getEncoder().encode(jsonBytes),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
