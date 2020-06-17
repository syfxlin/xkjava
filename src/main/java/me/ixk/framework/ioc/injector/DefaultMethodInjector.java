package me.ixk.framework.ioc.injector;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.PostConstruct;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.MethodInjector;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class DefaultMethodInjector implements MethodInjector {

    @Override
    public Object inject(
        Container container,
        Object instance,
        Map<String, Object> args
    ) {
        if (instance == null) {
            return null;
        }
        Set<Method> methods = ClassUtils.getMethods(instance);
        for (Method method : methods) {
            // Set 注入
            Autowired autowired = AnnotationUtils.getAnnotation(
                method,
                Autowired.class
            );
            if (autowired != null) {
                container.call(instance, method, Object.class);
            }

            // init 方法
            PostConstruct postConstruct = AnnotationUtils.getAnnotation(
                method,
                PostConstruct.class
            );
            if (postConstruct != null) {
                container.call(instance, method, Object.class);
            }
        }
        return instance;
    }
}
