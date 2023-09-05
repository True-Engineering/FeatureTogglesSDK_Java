package ru.trueengineering.feature.flag.starter.extractor;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author m.yastrebov
 */
public class ObjectMapperBuilder {

    public static ObjectMapper build() {
        final SimpleModule simpleModule = new SimpleModule();
        /**
         * для выполнения hibernate валидации во время десериализации json кастомизируем процесс десериализации
         */
        simpleModule.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                                                          JsonDeserializer<?> deserializer) {
                if (deserializer instanceof BeanDeserializer) {
                    return new BeanValidationDeserializer((BeanDeserializer) deserializer);
                }

                return deserializer;
            }
        });
        return Jackson2ObjectMapperBuilder.json()
                .failOnUnknownProperties(true)
                .failOnEmptyBeans(true)
                .modules(simpleModule)
                .build();

    }
}
