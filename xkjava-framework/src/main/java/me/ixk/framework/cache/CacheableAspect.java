/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Map;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.CacheConfig;
import me.ixk.framework.annotations.CachePut;
import me.ixk.framework.annotations.Cacheable;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Express;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * Cacheable 切面
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 2:25
 */
@Aspect(
    "@annotation(me.ixk.framework.annotations.Cacheable) || @annotation(me.ixk.framework.annotations.CachePut)"
)
public class CacheableAspect extends AbstractCacheAspect {

    public CacheableAspect(final XkJava app, final CacheManager cacheManager) {
        super(app, cacheManager);
    }

    @Override
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MergedAnnotation methodAnnotation = joinPoint.getMethodAnnotation();
        final Cacheable cacheable = methodAnnotation.getAnnotation(
            Cacheable.class
        );
        final CacheConfig cacheConfig = joinPoint
            .getClassAnnotation()
            .getAnnotation(CacheConfig.class);
        final Cache cache = this.getCache(cacheable.cacheName(), cacheConfig);
        final Map<String, Object> variables = this.getVariables(joinPoint);
        final Object key =
            this.getKey(
                    cacheable.key(),
                    cacheable.keyGenerator(),
                    cacheConfig,
                    joinPoint,
                    variables
                );
        Object result = methodAnnotation.hasAnnotation(CachePut.class)
            ? null
            : cache.get(key);
        // 有缓存就直接返回
        if (result != null) {
            return result;
        }
        // 无缓存则执行
        result = joinPoint.proceed(joinPoint.getArgs());
        // 如果不符合条件则不缓存直接返回
        if (
            !this.checkStoreCache(
                    cacheable.condition(),
                    cacheable.unless(),
                    variables
                )
        ) {
            return result;
        }
        // 符合条件则缓存
        cache.put(key, result);
        return result;
    }

    private boolean checkStoreCache(
        final String condition,
        final String unless,
        final Map<String, Object> variables
    ) {
        if (
            (condition == null || condition.isEmpty()) &&
            (unless == null || unless.isEmpty())
        ) {
            return true;
        }
        if (condition == null || condition.isEmpty()) {
            return !Express.evaluateApp(unless, Boolean.class, null, variables);
        }
        if (Express.evaluateApp(condition, Boolean.class, null, variables)) {
            if (unless == null || unless.isEmpty()) {
                return true;
            }
            return !Express.evaluateApp(unless, Boolean.class, null, variables);
        }
        return false;
    }
}
