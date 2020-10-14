/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MultiValueMap
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:10
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    /**
     * 获取一个
     *
     * @param key 键
     *
     * @return 值
     */
    default V getOne(K key) {
        List<V> values = this.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /**
     * 新增
     *
     * @param key   键
     * @param value 值
     */
    default void add(K key, V value) {
        List<V> values = this.getOrDefault(key, new ArrayList<>());
        values.add(value);
        this.put(key, values);
    }

    /**
     * 新增多个
     *
     * @param key    键
     * @param values 多个值
     */
    default void addAll(K key, List<V> values) {
        List<V> vList = this.getOrDefault(key, new ArrayList<>());
        vList.addAll(values);
        this.put(key, vList);
    }

    /**
     * 新增多个
     *
     * @param values 多个值
     */
    default void addAll(MultiValueMap<K, V> values) {
        for (Entry<K, List<V>> entry : values.entrySet()) {
            this.addAll(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 新增多个
     *
     * @param key    键
     * @param values 多个值
     */
    default void addAll(K key, V[] values) {
        this.addAll(key, Arrays.asList(values));
    }

    /**
     * 设置
     *
     * @param key    键
     * @param values 多个值
     */
    default void set(K key, List<V> values) {
        this.put(key, values);
    }

    /**
     * 设置
     *
     * @param key    键
     * @param values 多个值
     */
    default void set(K key, V[] values) {
        this.put(key, Arrays.asList(values));
    }

    /**
     * 设置
     *
     * @param key   键
     * @param value 值
     */
    default void set(K key, V value) {
        this.put(key, Collections.singletonList(value));
    }
}
