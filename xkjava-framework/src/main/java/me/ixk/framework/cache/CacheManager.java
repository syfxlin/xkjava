/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Collection;

/**
 * 缓存管理器
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:36
 */
public interface CacheManager {
    /**
     * 获取缓存
     *
     * @param name 缓存名称
     *
     * @return 缓存值
     */
    Cache getCache(String name);

    /**
     * 新增缓存
     *
     * @param cache 缓存
     */
    void setCache(Cache cache);

    /**
     * 删除缓存
     *
     * @param name 缓存名称
     */
    void removeCache(String name);

    /**
     * 获取所有缓存名称
     *
     * @return 所有缓存名称
     */
    Collection<Cache> getCaches();
}
