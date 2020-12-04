/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.SoftCache;

/**
 * 切面管理器
 * <p>
 * 管理切面的匹配和获取
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:25
 */
public class AspectManager {

    private static final SoftCache<Method, List<Advice>> METHOD_CACHE = new SoftCache<>();
    /**
     * 所有的切面列表
     */
    private final List<AdviceEntry> adviceList = new CopyOnWriteArrayList<>();
    private final XkJava app;

    public AspectManager(XkJava app) {
        this.app = app;
    }

    public void addAdvice(
        AspectPointcut pointcut,
        Class<? extends Advice> advice
    ) {
        adviceList.add(new AdviceEntry(pointcut, advice));
    }

    public List<Advice> getAdvices(Method method) {
        List<Advice> cache = METHOD_CACHE.get(method);
        if (cache != null) {
            return cache;
        } else {
            synchronized (METHOD_CACHE) {
                cache = METHOD_CACHE.get(method);
                if (cache != null) {
                    return cache;
                }
            }
        }
        synchronized (METHOD_CACHE) {
            List<Advice> list = new ArrayList<>();
            for (AdviceEntry entry : adviceList) {
                if (entry.getPointcut().matches(method)) {
                    list.add(this.app.make(entry.getAdvice()));
                }
            }
            METHOD_CACHE.put(method, list);
            return list;
        }
    }

    public boolean matches(Class<?> clazz) {
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(clazz)) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(Method method) {
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(method)) {
                return true;
            }
        }
        return false;
    }

    private static class AdviceEntry {

        private final AspectPointcut pointcut;

        private final Class<? extends Advice> advice;

        private final int order;

        public AdviceEntry(
            AspectPointcut pointcut,
            Class<? extends Advice> advice
        ) {
            this.pointcut = pointcut;
            this.advice = advice;
            Integer order = AnnotationUtils
                .getAnnotation(advice)
                .get(Order.class, "order");
            this.order =
                Objects.requireNonNullElse(order, Order.LOWEST_PRECEDENCE);
        }

        public AspectPointcut getPointcut() {
            return pointcut;
        }

        public Class<? extends Advice> getAdvice() {
            return advice;
        }

        public int getOrder() {
            return order;
        }
    }
}
