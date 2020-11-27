/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.cache.CacheManager;
import me.ixk.framework.cache.MapCacheManager;

/**
 * 缓存提供者
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 4:04
 */
@Provider
public class CacheProvider {

    // @Bean
    // RedisClient redisClient() {
    //     final RedisURI uri = RedisURI
    //         .builder()
    //         .withHost("localhost")
    //         .withPort(6379)
    //         .build();
    //     return RedisClient.create(uri);
    // }

    @Bean(name = "cacheManager")
    @ConditionalOnMissingBean(value = CacheManager.class, name = "cacheManager")
    public CacheManager cacheManager() {
        return new MapCacheManager();
    }
}
