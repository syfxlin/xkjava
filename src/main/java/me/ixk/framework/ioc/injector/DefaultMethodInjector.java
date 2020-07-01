package me.ixk.framework.ioc.injector;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.MethodInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.AnnotationUtils;

import java.lang.reflect.Method;

public class DefaultMethodInjector
    extends AbstractInjector
    implements MethodInjector {

    public DefaultMethodInjector(Container container) {
        super(container);
    }

    @Override
    public Object inject(Binding binding, Object instance, With with) {
        if (instance == null) {
            return null;
        }
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Set 注入
            Autowired autowired = AnnotationUtils.getAnnotation(
                method,
                Autowired.class
            );
            if (autowired != null) {
                container.call(
                    instance,
                    method,
                    Object.class,
                    with.getPrefix(),
                    with.getMap()
                );
            }
        }
        return instance;
    }
}
