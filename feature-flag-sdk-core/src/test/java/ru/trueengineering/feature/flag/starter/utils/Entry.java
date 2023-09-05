package ru.trueengineering.feature.flag.starter.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author m.yastrebov
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Entry<K, V> implements Map.Entry<K, V> {

    private K key;

    private V value;

    public static <U, P> Entry<U, P> of(U key, P value) {
        return new Entry<>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V previous = this.value;
        this.value = value;
        return previous;
    }
}
