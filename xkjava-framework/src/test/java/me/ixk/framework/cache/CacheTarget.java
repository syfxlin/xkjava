/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.cache.CacheConfig;
import me.ixk.framework.annotation.cache.CacheEvict;
import me.ixk.framework.annotation.cache.CachePut;
import me.ixk.framework.annotation.cache.Cacheable;
import me.ixk.framework.annotation.core.Component;

/**
 * @author Otstar Lin
 * @date 2020/11/27 下午 3:57
 */
@Slf4j
@Component
@CacheConfig(cacheName = "mapCache")
public class CacheTarget {

    @Cacheable(key = "key")
    public String key() {
        log.info("Invoke key");
        return "value";
    }

    @Cacheable(keyGenerator = StringKeyGenerator.class)
    public String keyGenerator() {
        log.info("Invoke keyGenerator");
        return "value";
    }

    @Cacheable(key = "condition", condition = "true")
    public String condition() {
        log.info("Invoke condition");
        return "value";
    }

    @Cacheable(key = "unless", unless = "false")
    public String unless() {
        log.info("Invoke unless");
        return "value";
    }

    @Cacheable(cacheName = "mapCache1", key = "cacheName")
    public String cacheName() {
        log.info("Invoke cacheName");
        return "value";
    }

    @Cacheable(key = "#{#key}")
    public String elKey(String key) {
        log.info("Invoke elKey");
        return "value";
    }

    @CachePut(key = "keyPut")
    public String keyPut() {
        log.info("Invoke put");
        return "value";
    }

    @CacheEvict(key = "key")
    public void evictKey() {}

    @CacheEvict(allEntries = true)
    public void evictAll() {}

    public static class StringKeyGenerator implements KeyGenerator {

        @Override
        public Object generateKey(
            Object target,
            Method method,
            Object... params
        ) {
            return "keyGenerator";
        }
    }
}
