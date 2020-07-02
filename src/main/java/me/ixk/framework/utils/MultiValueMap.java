package me.ixk.framework.utils;

import java.util.*;

public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    default V getOne(K key) {
        List<V> values = this.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    default void add(K key, V value) {
        List<V> values = this.getOrDefault(key, new ArrayList<>());
        values.add(value);
        this.put(key, values);
    }

    default void addAll(K key, List<V> values) {
        List<V> vList = this.getOrDefault(key, new ArrayList<>());
        vList.addAll(values);
        this.put(key, vList);
    }

    default void addAll(MultiValueMap<K, V> values) {
        for (Map.Entry<K, List<V>> entry : values.entrySet()) {
            this.addAll(entry.getKey(), entry.getValue());
        }
    }

    default void addAll(K key, V[] values) {
        this.addAll(key, Arrays.asList(values));
    }

    default void set(K key, List<V> values) {
        this.put(key, values);
    }

    default void set(K key, V[] values) {
        this.put(key, Arrays.asList(values));
    }

    default void set(K key, V value) {
        this.put(key, Collections.singletonList(value));
    }
}
