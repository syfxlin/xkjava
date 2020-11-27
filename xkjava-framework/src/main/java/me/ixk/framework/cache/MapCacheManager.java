/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map 缓存管理器
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:42
 */
public class MapCacheManager implements CacheManager {
    private final Map<String, Cache> caches = new ConcurrentHashMap<>(16);

    @Override
    public Cache getCache(final String name) {
        Cache cache = this.caches.get(name);
        if (cache == null) {
            cache = this.createMapCache(name);
            this.setCache(cache);
        }
        return cache;
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
        return new SimpleCacheCache(name);
    }
}
