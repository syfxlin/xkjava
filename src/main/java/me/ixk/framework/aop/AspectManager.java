package me.ixk.framework.aop;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AspectManager {
    protected Application app;

    protected static List<AdviceEntry> adviceList = new ArrayList<>();

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
                this.order = 0;
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

    public AspectManager(Application app) {
        this.app = app;
    }

    public Map<String, List<Advice>> matches(Class<?> _class) {
        Map<String, List<Advice>> map = new ConcurrentHashMap<>();
        for (AdviceEntry entry : adviceList) {
            if (entry.getPointcut().matches(_class)) {
                for (Method method : _class.getMethods()) {
                    this.addAdviceToMap(
                            method.getName(),
                            entry.getAdvice(),
                            map
                        );
                }
            } else {
                for (Method method : _class.getMethods()) {
                    if (entry.getPointcut().matches(method)) {
                        this.addAdviceToMap(
                                method.getName(),
                                entry.getAdvice(),
                                map
                            );
                    }
                }
            }
        }
        return map;
    }

    public void addAdviceToMap(
        String name,
        Advice advice,
        Map<String, List<Advice>> map
    ) {
        List<Advice> list = map.getOrDefault(name, new ArrayList<>());
        list.add(advice);
        map.put(name, list);
    }
}
