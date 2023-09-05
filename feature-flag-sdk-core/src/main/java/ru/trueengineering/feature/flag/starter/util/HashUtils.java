package ru.trueengineering.feature.flag.starter.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import ru.trueengineering.feature.flag.starter.model.FeatureFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;


@Slf4j
public class HashUtils {

    public static String getHash(String string) {
        return DigestUtils.sha256Hex(string);
    }

    public static String getHash(Collection<FeatureFlag> features) {
        if (isEmpty(features)) {
            return "";
        }

        List<FeatureFlag> sortedFeatures = new ArrayList<>(features);
        sortedFeatures.sort(Comparator.comparing(FeatureFlag::getUid));

        String encodeToString = getHash(sortedFeatures.toString());
        log.trace("hash of {} = {}", sortedFeatures, encodeToString);

        return encodeToString;
    }
}
