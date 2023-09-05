package ru.trueengineering.feature.flag.starter.extractor.file;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.trueengineering.feature.flag.starter.extractor.BeanValidationDeserializer;
import ru.trueengineering.feature.flag.starter.extractor.ObjectMapperBuilder;
import ru.trueengineering.feature.flag.starter.extractor.file.v1.JsonMapFileFeatureFlagsUnmarshaller;
import ru.trueengineering.feature.flag.starter.extractor.file.v2.FeaturesHolderFileFeatureFlagsUnmarshaller;

import java.util.List;

/**
 * @author m.yastrebov
 */
@Configuration
public class ExtractorFromFileConfiguration {

    @Bean
    @Primary
    public CompositeFileFeatureFlagsUnmarshaller compositeFeatureFLagsFromFileUnmarshaller(
            List<FileFeatureFlagsUnmarshaller> unmarshallers
    ) {
        return new CompositeFileFeatureFlagsUnmarshaller(unmarshallers);
    }

    @Bean
    public JsonMapFileFeatureFlagsUnmarshaller jsonMapFeatureFlagsUnmarshaller() {
        return new JsonMapFileFeatureFlagsUnmarshaller(ObjectMapperBuilder.build());
    }

    @Bean
    public FeaturesHolderFileFeatureFlagsUnmarshaller featuresHolderJsonUnmarshaller() {
        return new FeaturesHolderFileFeatureFlagsUnmarshaller(ObjectMapperBuilder.build());
    }

    private ObjectMapper objectMapper() {
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
