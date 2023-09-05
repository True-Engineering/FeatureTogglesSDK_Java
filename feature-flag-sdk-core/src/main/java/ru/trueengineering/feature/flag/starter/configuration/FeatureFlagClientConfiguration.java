package ru.trueengineering.feature.flag.starter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.kubernetes.config.ConfigMapConfigProperties;
import org.springframework.cloud.kubernetes.config.SecretsConfigProperties;
import org.springframework.cloud.kubernetes.config.reload.ConfigReloadProperties;
import org.springframework.cloud.kubernetes.config.reload.ConfigurationUpdateStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.annotation.RequestScope;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagConfigMapChangeDetector;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagFileChangeDetector;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagStateRefresher;
import ru.trueengineering.feature.flag.starter.extractor.FeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.extractor.FileFeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.extractor.file.ExtractorFromFileConfiguration;
import ru.trueengineering.feature.flag.starter.extractor.file.FileFeatureFlagsUnmarshaller;
import ru.trueengineering.feature.flag.starter.extractor.k8s.ConfigmapFeatureFlagExtractor;
import ru.trueengineering.feature.flag.starter.extractor.k8s.ExtractorFromConfigMapConfiguration;
import ru.trueengineering.feature.flag.starter.extractor.k8s.KubernetesFeatureFlagConfigMapUnmarshaller;
import ru.trueengineering.feature.flag.starter.model.EvaluatedFeatureFlagHolder;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContextHolder;
import ru.trueengineering.feature.flag.starter.propagation.context.Base64EvaluatedFeatureFlagHeaderHandler;
import ru.trueengineering.feature.flag.starter.propagation.context.EvaluatedFeatureFlagHeaderHandler;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagStoreConfigurationProperties;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagsSdkProperties;
import ru.trueengineering.feature.flag.starter.provider.EvaluatedFeatureFlagStateProvider;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagStateProvider;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;
import ru.trueengineering.feature.flag.starter.provider.MapFeatureFlagStateProvider;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagContextProvider;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategy;
import ru.trueengineering.feature.flag.starter.strategy.FeatureFlagStrategyProvider;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

/**
 * @author s.sharaev
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({FeatureFlagsSdkProperties.class, FeatureFlagScanNamesProperties.class})
@ComponentScan({"ru.trueengineering.feature.flag.starter"})
public class FeatureFlagClientConfiguration {

    @Configuration
    public static class FeatureFlagClientCommonAutoConfiguration {

        public static final String FEATURE_FLAG_REFRESH_STRATEGY = "featureFlagRefresh";

        @Bean
        public ConfigurationUpdateStrategy configurationUpdateStrategy(
                List<FeatureFlagStateRefresher> refreshers) {
            return new ConfigurationUpdateStrategy(FEATURE_FLAG_REFRESH_STRATEGY,
                    () -> refreshers.forEach(FeatureFlagStateRefresher::refreshFeatureFlags));
        }

        @Bean
        @RequestScope
        public EvaluatedFeatureFlagHolder featureFlagsContextHolder() {
            return new EvaluatedFeatureFlagHolder();
        }

        @Bean
        public EvaluatedFeatureFlagHeaderHandler featureFlagsContextHandler(
                Optional<ObjectMapper> objectMapperOptional,
                EvaluatedFeatureFlagHolder contextHolder) {
            return new Base64EvaluatedFeatureFlagHeaderHandler(objectMapperOptional.orElse(new ObjectMapper()),
                    contextHolder);
        }

        @Bean
        @ConditionalOnMissingBean(FeatureFlagContextProvider.class)
        FeatureFlagContextProvider featureFlagContextProvider() {
            return () -> new FeatureFlagContext(emptyMap());
        }

        @Bean
        public FeatureFlagsHolder featureFlagsHolder(
                FeatureFlagExtractor featureFlagExtractor) {
            return new FeatureFlagsHolder(featureFlagExtractor);
        }

        @Bean
        @RequestScope
        public FeatureFlagContextHolder featureFlagHolder(
                FeatureFlagContextProvider featureFlagContextProvider) {
            return new FeatureFlagContextHolder(featureFlagContextProvider.getFlippingExecutionContext());
        }

        @Bean
        public FeatureFlagStrategyProvider featureFlagStrategyProvider(List<FeatureFlagStrategy> strategyList) {
            Map<String, FeatureFlagStrategy> strategyMap = strategyList.stream()
                    .collect(Collectors.toMap(FeatureFlagStrategy::getClassName, Function.identity(), (t1, t2) -> t1));
            return new FeatureFlagStrategyProvider(strategyMap);
        }

        @Bean
        public FeatureFlagStateProvider featureFlagStateProvider(
                FeatureFlagsHolder featureFlagsHolder,
                EvaluatedFeatureFlagHolder contextHolder,
                FeatureFlagContextHolder featureFlagContextHolder,
                FeatureFlagStrategyProvider featureFlagStrategyProvider,
                FeatureFlagScanNamesProperties featureFlagScanNamesProperties) {
            return new EvaluatedFeatureFlagStateProvider(
                    new MapFeatureFlagStateProvider(featureFlagsHolder, featureFlagContextHolder,
                            featureFlagStrategyProvider, featureFlagScanNamesProperties),
                    contextHolder
            );
        }
    }

    @Configuration
    @Import({FeatureFlagClientCommonAutoConfiguration.class, ExtractorFromConfigMapConfiguration.class})
    @EnableConfigurationProperties({ConfigMapConfigProperties.class,
            ConfigReloadProperties.class,
            SecretsConfigProperties.class,
            FeatureFlagStoreConfigurationProperties.class})
    @ConditionalOnProperty(
            value = "feature.flag.store.type",
            havingValue = "configmap",
            matchIfMissing = true)
    public static class FeatureFlagClientK8sConfiguration {

        @Bean
        public FeatureFlagConfigMapChangeDetector changeDetector(AbstractEnvironment environment,
                ConfigReloadProperties configReloadProperties,
                KubernetesClient kubernetesClient,
                ConfigurationUpdateStrategy strategy,
                ConfigMapConfigProperties properties) {
            return new FeatureFlagConfigMapChangeDetector(environment,
                    configReloadProperties,
                    kubernetesClient,
                    strategy,
                    properties.getName());
        }

        @Bean
        public FeatureFlagExtractor featureFlagUpdater(KubernetesClient kubernetesClient,
                KubernetesFeatureFlagConfigMapUnmarshaller configMapUnmarshaller,
                ConfigMapConfigProperties configMapConfigProperties) {
            return new ConfigmapFeatureFlagExtractor(kubernetesClient, configMapUnmarshaller,
                    configMapConfigProperties);
        }
    }

    @Slf4j
    @Configuration
    @EnableScheduling
    @Import({FeatureFlagClientCommonAutoConfiguration.class, ExtractorFromFileConfiguration.class})
    @EnableConfigurationProperties({FeatureFlagStoreConfigurationProperties.class})
    @ConditionalOnProperty(value = "feature.flag.store.type", havingValue = "file")
    public static class FeatureFlagClientFileStoreConfiguration {

        @Bean
        public FeatureFlagFileChangeDetector changeDetector(
                ConfigurationUpdateStrategy configurationUpdateStrategy,
                Path featureFlagFilePath) throws IOException {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                // WatchService наблюдает только за директориями, поэтому регистрируем на родительскую директорию файла
                featureFlagFilePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                return new FeatureFlagFileChangeDetector(
                        configurationUpdateStrategy,
                        watchService,
                        featureFlagFilePath.getFileName().toString());
            } catch (IOException e) {
                log.error("Failed to create watchService!", e);
                throw e;
            }
        }

        @Bean
        public Path featureFlagFilePath(FeatureFlagStoreConfigurationProperties properties) {
            return Paths.get(properties.getFile().getName());
        }

        @Bean
        public FeatureFlagExtractor fileFeatureMap(FileFeatureFlagsUnmarshaller featureFlagsUnmarshaller,
                                                   Path featureFlagFilePath) {
            return new FileFeatureFlagExtractor(featureFlagsUnmarshaller, featureFlagFilePath);
        }
    }

}
