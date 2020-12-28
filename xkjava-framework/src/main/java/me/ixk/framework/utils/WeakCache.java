/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import io.github.imsejin.expression.util.ConcurrentReferenceHashMap;

/**
 * 弱引用缓存
 *
 * @author Otstar Lin
 * @date 2020/12/4 下午 10:40
 */
public class WeakCache<K, V> extends ConcurrentReferenceHashMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    public WeakCache() {
        super(
            DEFAULT_INITIAL_CAPACITY,
            DEFAULT_LOAD_FACTOR,
            DEFAULT_CONCURRENCY_LEVEL,
            ReferenceType.WEAK
        );
    }

    public WeakCache(int initialCapacity) {
        super(
            initialCapacity,
            DEFAULT_LOAD_FACTOR,
            DEFAULT_CONCURRENCY_LEVEL,
            ReferenceType.WEAK
        );
    }

    public WeakCache(int initialCapacity, float loadFactor) {
        super(
            initialCapacity,
            loadFactor,
            DEFAULT_CONCURRENCY_LEVEL,
            ReferenceType.WEAK
        );
    }

    public WeakCache(int initialCapacity, int concurrencyLevel) {
        super(
            initialCapacity,
            concurrencyLevel,
            DEFAULT_CONCURRENCY_LEVEL,
            ReferenceType.WEAK
        );
    }

    public WeakCache(
        int initialCapacity,
        float loadFactor,
        int concurrencyLevel
    ) {
        super(
            initialCapacity,
            loadFactor,
            concurrencyLevel,
            ReferenceType.WEAK
        );
    }
}
