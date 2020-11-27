/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import cn.hutool.core.lang.SimpleCache;
import me.ixk.framework.utils.Convert;

/**
 * 弱引用缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:48
 */
public class SimpleCacheCache implements Cache {
    private final SimpleCache<Object, Object> caches = new SimpleCache<>();
    private final String name;

    public SimpleCacheCache(String name) {
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
    public <T> T get(Object key, Class<T> returnType) {
        return Convert.convert(returnType, this.caches.get(key));
    }

    @Override
    public void put(Object key, Object value) {
        this.caches.put(key, value);
    }

    @Override
    public void evict(Object key) {
        this.caches.remove(key);
    }

    @Override
    public void clear() {
        this.caches.clear();
    }
}
