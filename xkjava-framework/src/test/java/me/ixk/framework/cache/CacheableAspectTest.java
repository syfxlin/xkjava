/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Enable;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/27 下午 3:56
 */
@XkJavaTest
@Enable(name = "cache")
class CacheableAspectTest {

    @Autowired
    CacheTarget cacheTarget;

    @Autowired
    CacheManager cacheManager;

    @Test
    void cache() {
        this.cacheTarget.key();
        final Cache mapCache = this.cacheManager.getCache("mapCache");
        assertEquals("value", mapCache.get("key"));
        this.cacheTarget.keyGenerator();
        assertEquals("value", mapCache.get("keyGenerator"));
        this.cacheTarget.condition();
        assertEquals("value", mapCache.get("condition"));
        this.cacheTarget.unless();
        assertEquals("value", mapCache.get("unless"));
        this.cacheTarget.cacheName();
        assertEquals(
            "value",
            this.cacheManager.getCache("mapCache1").get("cacheName")
        );
        this.cacheTarget.elKey("elKey");
        assertEquals("value", mapCache.get("elKey"));

        this.cacheTarget.key();

        this.cacheTarget.keyPut();
        this.cacheTarget.keyPut();

        this.cacheTarget.evictKey();
        assertNull(mapCache.get("key"));
        // this.cacheTarget.evictAll();
        // assertNull(mapCache.get("unless"));
    }
}
