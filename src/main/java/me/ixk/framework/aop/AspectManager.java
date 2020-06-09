package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Application;

public class AspectManager {
    protected Application app;

    protected List<AdviceEntry> adviceList;

    protected static class AdviceEntry {
        private final AspectPointcut pointcut;

        private final Advice advice;

        private final int order;

        public AdviceEntry(AspectPointcut pointcut, Advice advice) {
            this.pointcut = pointcut;
            this.advice = advice;
            Order order = advice.getClass().getAnnotation(Order.class);
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
        this.adviceList = new ArrayList<>();
        this.loadAdvice();
    }

    public Map<String, List<Advice>> matches(Class<?> _class) {
        Map<String, List<Advice>> map = new ConcurrentHashMap<>();
        for (AdviceEntry entry : this.adviceList) {
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

    protected void addAdviceToMap(
        String name,
        Advice advice,
        Map<String, List<Advice>> map
    ) {
        List<Advice> list = map.getOrDefault(name, new ArrayList<>());
        list.add(advice);
        map.put(name, list);
    }

    protected void loadAdvice() {
        this.app.getClassesByAnnotation(Aspect.class)
            .stream()
            .filter(Advice.class::isAssignableFrom)
            .forEach(
                adviceClass -> {
                    Aspect aspect = adviceClass.getAnnotation(Aspect.class);
                    this.adviceList.add(
                            new AdviceEntry(
                                new AspectPointcut(aspect.value()),
                                this.app.make(
                                        adviceClass.getName(),
                                        Advice.class
                                    )
                            )
                        );
                }
            );
        this.adviceList.sort(
                Comparator.comparingInt(AdviceEntry::getOrder).reversed()
            );
    }
}
