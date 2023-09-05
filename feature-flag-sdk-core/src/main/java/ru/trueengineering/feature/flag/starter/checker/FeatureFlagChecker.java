package ru.trueengineering.feature.flag.starter.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;
import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeatureFlagChecker {
    private final FeatureFlagsHolder holder;
    private final FeatureFlagScanNamesProperties scanNamesProperties;

    @PostConstruct
    public void checkFeatureFlags() {
        if (!scanNamesProperties.isEnabled() || scanNamesProperties.getPath() == null) {
            return;
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scanNamesProperties.getPath()))
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes));

        Set<Class<?>> interfaces = reflections.getTypesAnnotatedWith(FeatureFlagNames.class);

        for (Class<?> interfaceClass : interfaces) {
            Field[] fields = interfaceClass.getFields();

            for (Field field : fields) {
                if (field.getType().equals(String.class)) {
                    try {
                        String featureFlagName = (String) field.get(null);
                        if (!holder.hasFeatureFlag(featureFlagName)) {
                            log.warn("Feature flag " + featureFlagName + " is not found in the holder");
                        }
                    } catch (IllegalAccessException e) {
                        log.warn("Failed to access field " + field.getName() +
                                " of interface " + interfaceClass.getName());
                    }
                }
            }
        }
    }
}
