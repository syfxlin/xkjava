/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
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

    private static final SoftCache<Object, Boolean> MATCHES_CACHE = new SoftCache<>();
    private static final SoftCache<Method, List<Advice>> METHOD_CACHE = new SoftCache<>();
    /**
     * 所有的切面列表
     */
    private final List<AdviceEntry> adviceList = new CopyOnWriteArrayList<>();
    private final XkJava app;

    public AspectManager(final XkJava app) {
        this.app = app;
    }

    public void addAdvice(
        final AspectPointcut pointcut,
        final Class<? extends Advice> advice
    ) {
        adviceList.add(new AdviceEntry(pointcut, advice));
    }

    public List<Advice> getAdvices(final Method method) {
        return METHOD_CACHE.computeIfAbsent(
            method,
            m ->
                this.adviceList.stream()
                    .filter(e -> e.getPointcut().matches(m))
                    .map(e -> this.app.make(e.getAdvice()))
                    .collect(Collectors.toList())
        );
    }

    public boolean matches(final Class<?> clazz) {
        return MATCHES_CACHE.computeIfAbsent(
            clazz,
            c ->
                this.adviceList.stream()
                    .anyMatch(e -> e.getPointcut().matches((Class<?>) c))
        );
    }

    public boolean matches(final Method method) {
        return MATCHES_CACHE.computeIfAbsent(
            method,
            m ->
                this.adviceList.stream()
                    .anyMatch(e -> e.getPointcut().matches((Method) m))
        );
    }

    private static class AdviceEntry {

        private final AspectPointcut pointcut;

        private final Class<? extends Advice> advice;

        private final int order;

        public AdviceEntry(
            final AspectPointcut pointcut,
            final Class<? extends Advice> advice
        ) {
            this.pointcut = pointcut;
            this.advice = advice;
            final Integer order = MergedAnnotation
                .from(advice)
                .get(Order.class, "order", Integer.class);
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
