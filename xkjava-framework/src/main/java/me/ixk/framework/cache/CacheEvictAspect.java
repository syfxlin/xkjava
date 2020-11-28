/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Map;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.CacheConfig;
import me.ixk.framework.annotations.CacheEvict;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.XkJava;

/**
 * CacheEvict 切面
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 4:32
 */
@Aspect("@annotation(me.ixk.framework.annotations.CacheEvict)")
public class CacheEvictAspect extends AbstractCacheAspect {

    public CacheEvictAspect(
        XkJava app,
        CacheManager cacheManager,
        BeanExpressionResolver resolver
    ) {
        super(app, cacheManager, resolver);
    }

    @Override
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final CacheEvict cacheEvict = joinPoint
            .getMethodAnnotation()
            .getAnnotation(CacheEvict.class);
        final CacheConfig cacheConfig = joinPoint
            .getClassAnnotation()
            .getAnnotation(CacheConfig.class);
        final Cache cache = this.getCache(cacheEvict.cacheName(), cacheConfig);
        final Map<String, Object> variables = this.getVariables(joinPoint);
        if (
            !cacheEvict.condition().isEmpty() &&
            !this.expressionResolver.evaluate(
                    cacheEvict.condition(),
                    Boolean.class,
                    null,
                    variables
                )
        ) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        final boolean before = cacheEvict.beforeInvocation();
        if (cacheEvict.allEntries()) {
            if (before) {
                cache.invalidate();
            }
            final Object result = joinPoint.proceed(joinPoint.getArgs());
            if (!before) {
                cache.invalidate();
            }
            return result;
        }
        final Object key =
            this.getKey(
                    cacheEvict.key(),
                    cacheEvict.keyGenerator(),
                    cacheConfig,
                    joinPoint,
                    variables
                );
        if (before) {
            cache.evictIfPresent(key);
        }
        final Object result = joinPoint.proceed(joinPoint.getArgs());
        if (!before) {
            cache.evictIfPresent(key);
        }
        return result;
    }
}
