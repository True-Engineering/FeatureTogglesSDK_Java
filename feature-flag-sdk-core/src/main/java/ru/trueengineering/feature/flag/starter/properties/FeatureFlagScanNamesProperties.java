package ru.trueengineering.feature.flag.starter.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "feature.flag.scan")
public class FeatureFlagScanNamesProperties {
    /**
     * Включает обработку обращения из проекта несуществующего ФФга
     */
    private boolean enabled = false;

    /**
     * Путь до пакета, где лежат интерфейсы с объявленными фичафлагами
     */
    private String path = null;
}
