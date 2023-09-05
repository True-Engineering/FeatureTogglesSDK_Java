package ru.trueengineering.feature.flag.starter.extractor.file;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author m.yastrebov
 */
@Slf4j
@AllArgsConstructor
public final class CompositeFileFeatureFlagsUnmarshaller implements FileFeatureFlagsUnmarshaller {

    private final List<FileFeatureFlagsUnmarshaller> unmarshallers;

    @Override
    public FeatureFlags unmarshal(Path source) throws IOException {
        List<Exception> delayedExceptions = new ArrayList<>(unmarshallers.size());
        for (FileFeatureFlagsUnmarshaller unmarshaller : unmarshallers) {
            try {
                return unmarshaller.unmarshal(source);
            } catch (Exception ignored) {
                delayedExceptions.add(ignored);
            }
        }
        delayedExceptions.forEach(e ->
                log.error("Unable to fetch feature flags from file {} with unmarshallers", source, e)
        );
        throw new RuntimeException(String.format("Unable to fetch feature flags from file %s", source.toString()));
    }
}
