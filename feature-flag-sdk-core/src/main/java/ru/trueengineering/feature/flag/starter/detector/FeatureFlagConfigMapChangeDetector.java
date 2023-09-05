package ru.trueengineering.feature.flag.starter.detector;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.kubernetes.config.reload.ConfigReloadProperties;
import org.springframework.cloud.kubernetes.config.reload.ConfigurationChangeDetector;
import org.springframework.cloud.kubernetes.config.reload.ConfigurationUpdateStrategy;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author s.sharaev
 * Следит за изменением конфигмапы с фичефлагами
 */
@Slf4j
public class FeatureFlagConfigMapChangeDetector extends ConfigurationChangeDetector {

    private static final Long WATCH_RESTART_DELAY_MILLIS = 5000L;

    protected KubernetesClient kubernetesClient;
    protected ConfigurationUpdateStrategy strategy;
    private final String configmapName;
    private final FeatureFlagConfigMapWatcher watcher;
    private final AtomicBoolean isWatcherStarted;
    private final AtomicReference<Watch> watch;


    public FeatureFlagConfigMapChangeDetector(ConfigurableEnvironment environment,
                                              ConfigReloadProperties configReloadProperties,
                                              KubernetesClient kubernetesClient,
                                              ConfigurationUpdateStrategy strategy,
                                              String configmapName) {

        super(environment, configReloadProperties, kubernetesClient, strategy);
        this.kubernetesClient = kubernetesClient;
        this.configmapName = configmapName;
        this.watcher = new FeatureFlagConfigMapWatcher(
                configmapName,
                this::reloadProperties,
                this::onCLose);
        this.watch = new AtomicReference<>(null);
        this.isWatcherStarted = new AtomicBoolean(false);
    }

    @PostConstruct
    private void startWatcher() {
        try {
            log.debug("Starting watcher for resource {} ", configmapName);
            if (watch.get() != null) {
                log.debug("Stopping previous watcher");
                watch.get().close();
            }
            if (isWatcherStarted.get()) {
                log.debug("Watcher already started, delaying execution of new watcher");
                try {
                    Thread.sleep(WATCH_RESTART_DELAY_MILLIS);
                } catch (InterruptedException e) {
                    log.error("Reflector thread was interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            isWatcherStarted.set(true);
            watch.set(this.kubernetesClient.configMaps().watch(watcher));
        } catch (Exception e) {
            log.error("Error while establishing a connection to watch configMap {}!", configmapName, e);
            throw e;
        }
    }

    private void onCLose() {
        isWatcherStarted.set(false);
        startWatcher();
    }
}
