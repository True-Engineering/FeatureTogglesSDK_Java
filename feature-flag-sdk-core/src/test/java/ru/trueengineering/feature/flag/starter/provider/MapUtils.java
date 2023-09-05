package ru.trueengineering.feature.flag.starter.provider;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... entry) {
        return new HashMap<K, V>() {{
            Arrays.stream(entry).forEach(e -> put(e.getKey(), e.getValue()));
        }};
    }
}
