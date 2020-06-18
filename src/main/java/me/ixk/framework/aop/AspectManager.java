package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.utils.AnnotationUtils;

public class AspectManager {
    protected static final List<AdviceEntry> adviceList = new ArrayList<>();

    protected static final Map<Method, List<Advice>> methodCache = new ConcurrentHashMap<>(
        50
    );

    public static void addAdvice(AspectPointcut pointcut, Advice advice) {
        adviceList.add(new AdviceEntry(pointcut, advice));
    }

    protected static class AdviceEntry {
        private final AspectPointcut pointcut;

        private final Advice advice;

        private final int order;

        public AdviceEntry(AspectPointcut pointcut, Advice advice) {
            this.pointcut = pointcut;
            this.advice = advice;
            Order order = AnnotationUtils.getAnnotation(
                advice.getClass(),
                Order.class
            );
            if (order != null) {
                this.order = order.value();
            } else {
                this.order = Order.LOWEST_PRECEDENCE;
            }
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

    public static boolean matches(Class<?> _class) {
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(_class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(Method method) {
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(method)) {
                return true;
            }
        }
        return false;
    }

    public static List<Advice> getAdvices(Method method) {
        if (methodCache.containsKey(method)) {
            return methodCache.get(method);
        }
        List<Advice> list = new ArrayList<>();
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(method)) {
                list.add(entry.getAdvice());
            }
        }
        methodCache.put(method, list);
        return list;
    }
}
