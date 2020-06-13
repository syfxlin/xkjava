package me.ixk.framework.ioc.injector;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.MethodInjector;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

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
            Autowired autowired = AnnotationUtils.getAnnotation(
                method,
                Autowired.class
            );
            if (autowired == null) {
                continue;
            }
            container.call(instance, method, instance.getClass(), args);
        }
        return instance;
    }
}
