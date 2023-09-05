package ru.trueengineering.feature.flag.starter.detector;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FeatureFlagConfigMapWatcher implements Watcher<ConfigMap> {


    private final String configmapName;

    private final Runnable onChange;
    private final Runnable onClose;

    @Override
    public void eventReceived(Action action, ConfigMap resource) {
        if (Objects.equals(configmapName, resource.getMetadata().getName())) {
            log.info("Detected change in config maps");
            onChange.run();
        }
    }

    @Override
    public void onClose(KubernetesClientException exception) {
        log.debug("Watch closing");
        if (exception != null) {
            if (exception.getCode() != HttpURLConnection.HTTP_GONE) {
                // не нужно логировать ошибку HTTP_GONE - это ошибка связана с изменением версии  ресурса,
                // за которым мы наблюдаем - "too old resource version" и является нормальным поведением системы.
                // В этом случае нужно переподключиться к ресурсу
                log.error("Exception received during watch", exception);
            }
        }
        onClose.run();
    }
}
