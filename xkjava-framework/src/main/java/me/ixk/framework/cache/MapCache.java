/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import me.ixk.framework.util.Convert;
import me.ixk.framework.util.WeakCache;

/**
 * 弱引用缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:48
 */
public class MapCache implements Cache {

    private final WeakCache<Object, Object> caches = new WeakCache<>();
    private final String name;

    public MapCache(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.caches;
    }

    @Override
    public <T> T get(final Object key, final Class<T> returnType) {
        return Convert.convert(returnType, this.caches.get(key));
    }

    @Override
    public void put(final Object key, final Object value) {
        this.caches.put(key, value);
    }

    @Override
    public void evict(final Object key) {
        this.caches.remove(key);
    }

    @Override
    public void clear() {
        this.caches.clear();
    }
}
