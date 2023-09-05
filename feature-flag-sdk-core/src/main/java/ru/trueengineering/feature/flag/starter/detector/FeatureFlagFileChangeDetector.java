package ru.trueengineering.feature.flag.starter.detector;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.kubernetes.config.reload.ConfigurationUpdateStrategy;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

/**
 * @author s.sharaev
 * Следит за изменением файла с фичефлагами
 */
@Slf4j
@AllArgsConstructor
public class FeatureFlagFileChangeDetector {

    public static final long POLL_TIMEOUT = 25L;

    private final ConfigurationUpdateStrategy updateStrategy;
    private final WatchService watchService;
    private final String fileName;

    @PostConstruct
    public void init() {
        log.info("Added new file watch");
    }

    @Scheduled(fixedDelayString = "${feature.flag.store.file.reload-period:1000}")
    public void checkUpdate() {
        log.debug("Check file {} for update", fileName);
        WatchKey watchKey = null;
        try {
            watchKey = watchService.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            log.error("Failed to check file {} for update!", fileName, e);
        }
        if (nonNull(watchKey)) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                final Path changed = (Path) event.context();

                if (changed.endsWith(fileName)) {
                    reload();
                }
            }
            watchKey.reset();
        }
    }

    private void reload() {
        log.info("Reloading using strategy: " + this.updateStrategy.getName());
        this.updateStrategy.reload();
    }
}
