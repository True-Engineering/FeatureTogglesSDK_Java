package ru.trueengineering.feature.flag.starter.extractor.k8s;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.trueengineering.feature.flag.starter.extractor.BaseUnmarshallerSpec;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagFormat;
import ru.trueengineering.feature.flag.starter.extractor.ObjectMapperBuilder;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author m.yastrebov
 */
@SpringJUnitConfig(ExtractorFromConfigMapConfiguration.class)
public interface BaseKubernetesConfigMapUnmarshallerSpec extends BaseUnmarshallerSpec {

    ObjectMapper OBJECT_MAPPER = ObjectMapperBuilder.build();

    KubernetesFeatureFlagConfigMapUnmarshaller getUut();

    @TestFactory
    default Collection<DynamicTest> shouldUnmarshalFile() {
        return validFormats().stream()
                .map(testCase -> dynamicTest(
                        "Должны успешно десериализовать данные с фича флагами из конфиг мапы " + testCase,
                        () -> shouldUnmarshal(testCase))
                ).collect(Collectors.toList());
    }

    @TestFactory
    default Collection<DynamicTest> shouldNotUnmarshalFile() {
        return Arrays.stream(FeatureFlagFormat.values())
                .filter(it -> !validFormats().contains(it))
                .map(testCase -> dynamicTest(
                        "Конфиг мапа не должна быть успешно десериализована, так как формат неподходящий " + testCase,
                        () -> shouldReturnNullFeatureFlagsForUnknownFormatTestTemplate(testCase))
                ).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Если config map is null возвращаем пустой список флагов")
    default void shouldThrowIfSourceIsNull() {
        assertThatThrownBy(() -> getUut().unmarshal(null))
                .isInstanceOf(RuntimeException.class)
                        .hasMessage("Unable to fetch feature flags, configmap is null!");
    }

    default void shouldUnmarshal(FeatureFlagFormat testCase) throws Exception {
        final FeatureFlags featureFlags = getUut().unmarshal(testCase.toConfigMap(OBJECT_MAPPER));
        verifyFeatureFlags(featureFlags);
    }

    @SneakyThrows
    default void shouldReturnNullFeatureFlagsForUnknownFormatTestTemplate(FeatureFlagFormat testCase) {
        assertThatThrownBy(() -> getUut().unmarshal(testCase.toConfigMap(OBJECT_MAPPER)))
                .isInstanceOf(RuntimeException.class);
    }

}
