package me.ixk.framework.ioc.injector;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.SkipPropertyAutowired;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.PropertyInjector;
import me.ixk.framework.ioc.With;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultPropertyInjector implements PropertyInjector {

    @Override
    public Object inject(Container container, Object instance, With with) {
        if (instance == null) {
            return null;
        }
        Class<?> instanceClass = ClassUtils.getUserClass(instance);
        if (
            instanceClass.getAnnotation(SkipPropertyAutowired.class) != null
        ) {
            return instance;
        }
        Field[] fields = instanceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(SkipPropertyAutowired.class) != null) {
                continue;
            }
            Autowired autowired = AnnotationUtils.getAnnotation(
                field,
                Autowired.class
            );
            PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                instanceClass,
                field.getName()
            );
            if (propertyDescriptor == null) {
                continue;
            }
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null && autowired == null) {
                continue;
            }
            Object dependency;
            if (autowired != null && !autowired.name().equals("")) {
                dependency = container.make(autowired.name(), field.getType());
            } else {
                Class<?> autowiredClass;
                if (autowired == null || autowired.type() == Class.class) {
                    autowiredClass = field.getType();
                } else {
                    autowiredClass = autowired.type();
                }
                dependency =
                    container.getInjectValue(
                        autowiredClass,
                        field.getName(),
                        with
                    );
            }
            if (writeMethod != null) {
                ReflectUtil.invoke(instance, writeMethod, dependency);
            } else {
                ReflectUtil.setFieldValue(instance, field, dependency);
            }
        }
        return instance;
    }
}
