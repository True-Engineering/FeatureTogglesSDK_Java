package ru.trueengineering.feature.flag.starter.extractor;

import ru.trueengineering.feature.flag.starter.model.FeatureFlag;
import ru.trueengineering.feature.flag.starter.model.FeatureFlags;
import ru.trueengineering.feature.flag.starter.utils.Entry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author m.yastrebov
 */
public interface BaseUnmarshallerSpec {

    List<FeatureFlagFormat> validFormats();

    default void verifyFeatureFlags(FeatureFlags featureFlags) {
        assertThat(featureFlags).isNotNull();
        assertThat(featureFlags.getFeatureMap()).hasSize(1);
        assertThat(featureFlags.getFeatureMap()).containsOnlyKeys("ff.id");
        final FeatureFlag flag = featureFlags.getFeatureMap().get("ff.id");
        assertThat(flag.getDescription()).isEqualTo("some description");
        assertThat(flag.getGroup()).isEqualTo("group #1");
        assertThat(flag.getPermissions()).containsOnly("roler_1");
        assertThat(flag.getCustomProperties()).containsOnly(
                Entry.of("key 1", "value_1"), Entry.of("key 2", "value_2")
        );
        assertThat(flag.getFlippingStrategy().getClassName()).isEqualTo("name");
        assertThat(flag.getFlippingStrategy().getInitParams()).containsOnly(
                Entry.of("param_1", "value_1"), Entry.of("param 2", "value_2")
        );
    }

}
