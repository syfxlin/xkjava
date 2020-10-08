/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import cn.hutool.core.lang.SimpleCache;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.utils.AnnotationUtils;

public class AspectManager {
    protected final List<AdviceEntry> adviceList = new ArrayList<>();

    protected final SimpleCache<Method, List<Advice>> METHOD_CACHE = new SimpleCache<>();

    public void addAdvice(AspectPointcut pointcut, Advice advice) {
        adviceList.add(new AdviceEntry(pointcut, advice));
    }

    protected class AdviceEntry {
        private final AspectPointcut pointcut;

        private final Advice advice;

        private final int order;

        public AdviceEntry(AspectPointcut pointcut, Advice advice) {
            this.pointcut = pointcut;
            this.advice = advice;
            Integer order = AnnotationUtils.getAnnotationValue(
                advice.getClass(),
                Order.class,
                "order"
            );
            this.order =
                Objects.requireNonNullElse(order, Order.LOWEST_PRECEDENCE);
        }

        public AspectPointcut getPointcut() {
            return pointcut;
        }

        public Advice getAdvice() {
            return advice;
        }

        public int getOrder() {
            return order;
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

    public List<Advice> getAdvices(Method method) {
        List<Advice> cache = METHOD_CACHE.get(method);
        if (cache != null) {
            return cache;
        }
        List<Advice> list = new ArrayList<>();
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(method)) {
                list.add(entry.getAdvice());
            }
        }
        METHOD_CACHE.put(method, list);
        return list;
    }
}
