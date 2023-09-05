package ru.trueengineering.feature.flag.starter.extractor.file;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.trueengineering.feature.flag.starter.extractor.BaseUnmarshallerSpec;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagFormat;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author m.yastrebov
 */
@SpringJUnitConfig(ExtractorFromFileConfiguration.class)
public interface BaseJsonFileUnmarshallerSpec extends BaseUnmarshallerSpec {

    FileFeatureFlagsUnmarshaller getUut();

    @TestFactory
    default Collection<DynamicTest> shouldUnmarshalFile() {
        return validFormats().stream()
                .map(testCase -> dynamicTest(
                        "Должны успешно десериализовать файл с фича флагами " + testCase +
                                ": " + testCase.getJsonPath(),
                        () -> shouldUnmarshal(testCase))
                ).collect(Collectors.toList());
    }

    @TestFactory
    default Collection<DynamicTest> shouldNotUnmarshalFile() {
        return Arrays.stream(FeatureFlagFormat.values())
                .filter(it -> !validFormats().contains(it))
                .map(testCase -> dynamicTest(
                        "Файл не должен быть успешно десериализован, так как формат json неподходящий " + testCase + 
                                ": " + testCase.getJsonPath(),
                        () -> shouldThrowsExceptionForInvalidJsonTestTemplate(testCase))
                ).collect(Collectors.toList());
    }


    default void shouldUnmarshal(FeatureFlagFormat testCase) throws Exception {
        final FeatureFlags featureFlags = getUut().unmarshal(testCase.toPath());
        verifyFeatureFlags(featureFlags);
    }


    default void shouldThrowsExceptionForInvalidJsonTestTemplate(FeatureFlagFormat testCase) {
        assertThatThrownBy(() -> getUut().unmarshal(testCase.toPath())).isInstanceOf(IOException.class);
    }

}
