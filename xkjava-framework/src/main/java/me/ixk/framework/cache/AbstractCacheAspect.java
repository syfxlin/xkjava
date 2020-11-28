/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.CacheConfig;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.expression.BeanExpressionResolver;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.ParameterNameDiscoverer;

/**
 * @author Otstar Lin
 * @date 2020/11/27 下午 4:58
 */
public abstract class AbstractCacheAspect implements Advice {

    protected final XkJava app;
    protected final CacheManager cacheManager;
    protected final BeanExpressionResolver expressionResolver;

    public AbstractCacheAspect(
        final XkJava app,
        final CacheManager cacheManager,
        final BeanExpressionResolver resolver
    ) {
        this.app = app;
        this.cacheManager = cacheManager;
        this.expressionResolver = resolver;
    }

    protected Cache getCache(String cacheName, final CacheConfig cacheConfig) {
        if (cacheName.isEmpty() && cacheConfig != null) {
            cacheName = cacheConfig.cacheName();
        }
        final Cache cache = this.cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException(
                "Cache [" + cacheName + "] not found"
            );
        }
        return cache;
    }

    protected Map<String, Object> getVariables(ProceedingJoinPoint joinPoint) {
        // 生成用于 EL 语句的变量
        final String[] names = ParameterNameDiscoverer.getParameterNames(
            joinPoint.getMethod()
        );
        final Object[] args = joinPoint.getArgs();
        final Map<String, Object> variables = new ConcurrentHashMap<>(
            args.length + 1
        );
        for (int i = 0; i < names.length; i++) {
            variables.put(names[i], args[i]);
        }
        variables.put("joinPoint", joinPoint);
        return variables;
    }

    protected Object getKey(
        final String key,
        Class<? extends KeyGenerator> keyGenerator,
        CacheConfig cacheConfig,
        ProceedingJoinPoint joinPoint,
        Map<String, Object> variables
    ) {
        if (cacheConfig != null) {
            if (keyGenerator == KeyGenerator.class) {
                keyGenerator = cacheConfig.keyGenerator();
            }
        }
        if (keyGenerator != KeyGenerator.class) {
            return this.app.make(keyGenerator)
                .generateKey(
                    joinPoint.getTarget(),
                    joinPoint.getMethod(),
                    joinPoint.getArgs()
                );
        }
        if (!key.isEmpty()) {
            return this.expressionResolver.evaluate(
                    key,
                    Object.class,
                    null,
                    variables
                );
        }
        return Cache.DEFAULT_KEY;
    }
}
