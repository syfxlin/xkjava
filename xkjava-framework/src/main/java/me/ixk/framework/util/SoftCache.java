/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import io.github.imsejin.expression.util.ConcurrentReferenceHashMap;

/**
 * 软引用缓存
 *
 * @author Otstar Lin
 * @date 2020/12/2 下午 12:14
 */
public class SoftCache<K, V> extends ConcurrentReferenceHashMap<K, V> {

    public SoftCache() {
        super();
    }

    public SoftCache(int initialCapacity) {
        super(initialCapacity);
    }

    public SoftCache(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SoftCache(int initialCapacity, int concurrencyLevel) {
        super(initialCapacity, concurrencyLevel);
    }

    public SoftCache(
        int initialCapacity,
        float loadFactor,
        int concurrencyLevel
    ) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }
}
