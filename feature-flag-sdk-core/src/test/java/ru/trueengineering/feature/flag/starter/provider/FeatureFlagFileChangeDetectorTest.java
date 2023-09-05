package ru.trueengineering.feature.flag.starter.provider;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.kubernetes.config.reload.ConfigurationUpdateStrategy;
import ru.trueengineering.feature.flag.starter.detector.FeatureFlagFileChangeDetector;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class FeatureFlagFileChangeDetectorTest {

    public static final String FILE_NAME = "test-file.json";
    public static final String FILE_DIR = "test_dir";
    public static final String FILE_PATH = FILE_DIR + File.separator + FILE_NAME;
    public static final String OTHER_FILE_PATH = FILE_DIR + File.separator + "other.file";

    private FeatureFlagFileChangeDetector featureFlagFileChangeDetector;

    @Mock
    private WatchService watchService;
    @Mock
    private ConfigurationUpdateStrategy updateStrategy;
    @Mock
    private WatchKey watchKey;
    @Mock
    private WatchEvent watchEvent;

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        featureFlagFileChangeDetector = new FeatureFlagFileChangeDetector(updateStrategy, watchService, FILE_NAME);
        when(watchService.poll(anyLong(), any())).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(singletonList(watchEvent));
    }

    @Test
    public void checkFileUpdate() {
        when(watchEvent.context()).thenReturn(Paths.get(FILE_PATH));
        featureFlagFileChangeDetector.checkUpdate();
        Mockito.verify(updateStrategy, times(1)).reload();
        Mockito.verify(watchKey, times(1)).reset();
    }

    @Test
    public void checkFileNotUpdate() {
        when(watchEvent.context()).thenReturn(Paths.get(OTHER_FILE_PATH));
        featureFlagFileChangeDetector.checkUpdate();
        Mockito.verify(updateStrategy, times(0)).reload();
        Mockito.verify(watchKey, times(1)).reset();
    }

    @Test
    @SneakyThrows
    public void checkFileNoEvents() {
        when(watchService.poll(anyLong(), any())).thenReturn(null);
        featureFlagFileChangeDetector.checkUpdate();
        Mockito.verify(updateStrategy, times(0)).reload();
        Mockito.verify(watchKey, times(0)).reset();
    }
}