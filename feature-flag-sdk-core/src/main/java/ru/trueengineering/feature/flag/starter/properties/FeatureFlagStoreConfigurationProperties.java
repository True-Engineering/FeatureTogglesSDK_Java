package ru.trueengineering.feature.flag.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author s.sharaev
 */
@Data
@ConfigurationProperties(prefix = "feature.flag.store")
public class FeatureFlagStoreConfigurationProperties {

    /**
     * Тип хранения фиче флагов
     */
    private FeatureFlagStoreType type;

    private File file;

    @Data
    public static class File {

        /**
         * Включает чтение фичефлагов из файла
         */
        private boolean enabled;

        /**
         * Имя файла с фиче флагами
         */
        private String name;

        /**
         * Периодичность проверки изменений файла, в миллисекундах
         * @see ru.trueengineering.feature.flag.starter.detector.FeatureFlagFileChangeDetector
         */
        private long reloadPeriod;
    }

    public enum FeatureFlagStoreType {
        /**
         * Чтение фиче флагов из конфигмапы k8s
         */
        CONFIGMAP,
        /**
         * Чтение фичефлагов из файла
         */
        FILE
    }
}
