package ru.trueengineering.feature.flag.starter.checker;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagScanNamesProperties;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@FeatureFlagNames
interface TestFeatureFlagNames {
    String EXISTING = "existing";
    String NON_EXISTING = "non-existing";
}

public class FeatureFlagNamesCheckerTest {

    private final Logger log = (Logger) LoggerFactory.getLogger(FeatureFlagChecker.class);

    private FeatureFlagChecker featureFlagChecker;

    @Mock
    private FeatureFlagsHolder holder;

    @Mock
    private FeatureFlagScanNamesProperties properties;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        featureFlagChecker = new FeatureFlagChecker(holder, properties);
    }

    @Test
    void checkFeatureFlagNames() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        when(properties.isEnabled()).thenReturn(true);
        when(properties.getPath()).thenReturn("ru.trueengineering.feature.flag.starter.checker");
        when(holder.hasFeatureFlag("existing")).thenReturn(true);
        when(holder.hasFeatureFlag("non-existing")).thenReturn(false);

        featureFlagChecker.checkFeatureFlags();

        assertThat(listAppender.list.size()).isEqualTo(1);
        Assertions.assertThat(listAppender.list.get(0).getLevel().toString()).isEqualTo("WARN");
        Assertions.assertThat(listAppender.list.get(0).getFormattedMessage()).isEqualTo(
                "Feature flag non-existing is not found in the holder");
    }

}
