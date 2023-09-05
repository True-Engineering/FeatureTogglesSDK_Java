package ru.trueengineering.feature.flag.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "feature.flag")
public class FeatureFlagsSdkProperties {
    /**
     * Имя хэдера для хэша состояния фичефлагов
     */
    private String hashHeader = "FF-Hash";

    /**
     * Имя хэдера для фильтра по тегам
     */
    private String tagHeader = "FF-Tag";

    private Controller controller = new Controller();

    @Data
    public static class Controller {

        /**
         * Включает контроллер для получения состояния всех фичефлагов
         * @see ru.trueengineering.feature.flag.starter.controller.FeatureFlagsController
         */
        private boolean enabled = false;

    }
}
