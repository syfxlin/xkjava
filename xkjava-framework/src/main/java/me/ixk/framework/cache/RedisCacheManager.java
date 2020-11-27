/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import io.lettuce.core.RedisClient;

/**
 * Redis 缓存管理器
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 6:34
 */
public class RedisCacheManager extends MapCacheManager {
    private final RedisClient redisClient;

    public RedisCacheManager(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    protected Cache createMapCache(String name) {
        return new RedisCache(name, this.redisClient);
    }
}
