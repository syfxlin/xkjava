/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Map 缓存管理器
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:42
 */
public class MapCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>(
        16
    );

    @Override
    public Cache getCache(final String name) {
        return this.caches.computeIfAbsent(name, this::createMapCache);
    }

    @Override
    public void setCache(final Cache cache) {
        this.caches.put(cache.getName(), cache);
    }

    @Override
    public void removeCache(final String name) {
        this.caches.remove(name);
    }

    @Override
    public Collection<Cache> getCaches() {
        return this.caches.values();
    }

    protected Cache createMapCache(final String name) {
        return new MapCache(name);
    }
}
