package ru.trueengineering.feature.flag.starter.extractor.file;

import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author m.yastrebov
 */
public interface FileFeatureFlagsUnmarshaller {

    /**
     * Десериализация флагов из файла
     *
     * @param source путь до файла
     * @return фича флаги
     */
    FeatureFlags unmarshal(Path source) throws IOException;

}
