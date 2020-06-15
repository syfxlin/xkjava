package me.ixk.framework.ioc.injector;

import java.lang.reflect.Field;
import java.util.Map;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.PropertyInjector;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

public class DefaultPropertyInjector implements PropertyInjector {

    @Override
    public Object inject(
        Container container,
        Object instance,
        Map<String, Object> args
    ) {
        if (instance == null) {
            return null;
        }
        Field[] fields = ClassUtils.getUserClass(instance).getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = AnnotationUtils.getAnnotation(
                field,
                Autowired.class
            );
            if (autowired == null) {
                continue;
            }
            Object dependency;
            if (!autowired.name().equals("")) {
                dependency = container.make(autowired.name());
            } else {
                Class<?> autowiredClass;
                if (autowired.type() == Class.class) {
                    autowiredClass = field.getType();
                } else {
                    autowiredClass = autowired.type();
                }
                dependency = container.make(autowiredClass);
            }
            boolean originAccessible = field.canAccess(instance);
            field.setAccessible(true);
            try {
                field.set(instance, dependency);
            } catch (IllegalAccessException e) {
                throw new ContainerException("Object field setting failed", e);
            }
            field.setAccessible(originAccessible);
        }
        return instance;
    }
}
