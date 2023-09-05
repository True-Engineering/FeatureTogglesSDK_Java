package ru.trueengineering.feature.flag.starter.propagation.context;

import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlags;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Интерфейс добавления и извлечения ранее вычисленных фичефлагов в/из http запросов.
 * @see EvaluatedFeatureFlags
 */
public interface EvaluatedFeatureFlagHeaderHandler {
    /**
     * Интерфейс для вставки сериализованного контекста в запрос
     */
    @FunctionalInterface
    interface ContextInjector {
        /**
         * Вставляет сериализованный контекст в хэдеры запроса
         *
         * @param headerName        название хэдера
         * @param serializedContext сереализованный контекст
         * @throws IOException      в случае ошибки при вставке контекста
         */
        void inject(String headerName, String serializedContext) throws IOException;
    }

    /**
     * Сериализует контекст фичефлагов и вставляет в запрос
     * @param contextInjector   интерфейс вставки сериализованного контекста
     */
    void serializeContext(ContextInjector contextInjector);

    /**
     * Извлекает контекст фичефлагов из запроса
     * @param request   запрос
     */
    void deserializeContext(HttpServletRequest request);
}
