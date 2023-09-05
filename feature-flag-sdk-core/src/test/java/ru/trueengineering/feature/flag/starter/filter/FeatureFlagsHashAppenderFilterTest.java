package ru.trueengineering.feature.flag.starter.filter;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.properties.FeatureFlagsSdkProperties;
import ru.trueengineering.feature.flag.starter.provider.FeatureFlagsHolder;
import ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static ru.trueengineering.feature.flag.starter.provider.TestFeatureFlagsProvider.provideFeatureFlags;

public class FeatureFlagsHashAppenderFilterTest {

    private final FeatureFlags featureFlags = provideFeatureFlags();
    @Mock
    private FeatureFlagsHolder featureFlagsHolder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(featureFlagsHolder.getFeatureFlags()).thenReturn(featureFlags);
    }

    @SneakyThrows
    @Test
    void checkFilterAppendsHash() {
        FeatureFlagsSdkProperties props = new FeatureFlagsSdkProperties();
        FeatureFlags featureFlags = TestFeatureFlagsProvider.provideFeatureFlags();
        FeatureFlagsHashAppenderFilter filter = new FeatureFlagsHashAppenderFilter(props, featureFlagsHolder);

        HttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(
                new MockHttpServletRequest(),
                response,
                new MockFilterChain()
        );

        assertThat(response.getHeader(props.getHashHeader()))
                .isEqualTo(featureFlags.getFlagsHash());
    }
}
