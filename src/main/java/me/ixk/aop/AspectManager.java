package me.ixk.aop;

import me.ixk.annotations.Aspect;
import me.ixk.annotations.Order;
import me.ixk.ioc.Application;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AspectManager {
    protected Application app;

    protected List<AdviceEntry> adviceList;

    protected static class AdviceEntry {
        private final AspectPointcut pointcut;

        private final AdviceInterface advice;

        private final int order;

        public AdviceEntry(AspectPointcut pointcut, AdviceInterface advice) {
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

        public AdviceInterface getAdvice() {
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

    public Object weavingAspect(Class<?> _class) throws Throwable {
        Map<String, List<AdviceInterface>> map = this.matches(_class);
        Constructor<?>[] constructors = _class.getDeclaredConstructors();
        if (constructors.length != 1) {
            // 不允许构造器重载
            throw new RuntimeException(
                "The bound instance must have only one constructor"
            );
        }
        if (map.isEmpty()) {
            return constructors[0].newInstance();
        }
        return Enhancer.create(
            _class,
            constructors[0].getParameterTypes(),
            new DynamicInterceptor(map)
        );
    }

    protected Map<String, List<AdviceInterface>> matches(Class<?> _class) {
        Map<String, List<AdviceInterface>> map = new ConcurrentHashMap<>();
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
        AdviceInterface advice,
        Map<String, List<AdviceInterface>> map
    ) {
        List<AdviceInterface> list = map.getOrDefault(name, new ArrayList<>());
        list.add(advice);
        map.put(name, list);
    }

    protected void loadAdvice() {
        this.app.getClassesByAnnotation(Aspect.class)
            .stream()
            .filter(AdviceInterface.class::isAssignableFrom)
            .forEach(
                adviceClass -> {
                    Aspect aspect = adviceClass.getAnnotation(Aspect.class);
                    this.adviceList.add(
                            new AdviceEntry(
                                new AspectPointcut(aspect.value()),
                                this.app.make(
                                        adviceClass.getName(),
                                        AdviceInterface.class
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
