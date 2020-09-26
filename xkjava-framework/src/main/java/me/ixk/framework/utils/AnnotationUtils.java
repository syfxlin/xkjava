/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Order;

public abstract class AnnotationUtils extends AnnotationUtil {
    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Order or1 = null, or2 = null;
        if (o1 instanceof Class && o2 instanceof Class) {
            or1 = getAnnotation((Class<?>) o1, Order.class);
            or2 = getAnnotation((Class<?>) o2, Order.class);
        } else if (o1 instanceof Method && o2 instanceof Method) {
            or1 = getAnnotation((Method) o1, Order.class);
            or2 = getAnnotation((Method) o1, Order.class);
        }
        int i1 = or1 == null ? Order.LOWEST_PRECEDENCE : or1.value();
        int i2 = or2 == null ? Order.LOWEST_PRECEDENCE : or2.value();
        return i1 - i2;
    };

    public static <T extends Annotation> T getAnnotation(
        Parameter parameter,
        Class<T> annotationType
    ) {
        return parseAnnotation(parameter.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Field field,
        Class<T> annotationType
    ) {
        return parseAnnotation(field.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Constructor<?> constructor,
        Class<T> annotationType
    ) {
        return parseAnnotation(constructor.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Method method,
        Class<T> annotationType
    ) {
        return parseAnnotation(method.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Class<?> _class,
        Class<T> annotationType
    ) {
        return parseAnnotation(_class.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T parseAnnotation(T annotation) {
        if (annotation == null) {
            return null;
        }
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        Map<String, Object> memberValues = getMemberValues(annotation);
        Method[] methodValues = annotationClass.getDeclaredMethods();
        for (Method method : methodValues) {
            if (method.getAnnotation(AliasFor.class) == null) {
                continue;
            }
            String name = method.getName();
            if (isDefaultValue(method, memberValues)) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                String alias = aliasFor.value();
                if (aliasFor.annotation() != Annotation.class) {
                    Object object = getAnnotationValue(
                        parseAnnotation(
                            annotationClass.getAnnotation(aliasFor.annotation())
                        ),
                        alias.length() == 0 ? name : alias
                    );
                    memberValues.put(name, object);
                } else {
                    memberValues.put(name, memberValues.get(alias));
                }
            }
        }
        return annotation;
    }

    public static Object getAnnotationValue(Annotation annotation, String key) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        try {
            return ReflectUtil.invoke(annotation, key);
        } catch (Exception e) {
            for (Annotation item : annotationType.getAnnotations()) {
                Class<? extends Annotation> itemType = item.annotationType();
                if (
                    itemType == Documented.class ||
                    itemType == Inherited.class ||
                    itemType == Native.class ||
                    itemType == Repeatable.class ||
                    itemType == Retention.class ||
                    itemType == Target.class
                ) {
                    continue;
                }
                Object value = getAnnotationValue(item, key);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMemberValues(Annotation annotation) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(
                annotation
            );
            Field field = invocationHandler
                .getClass()
                .getDeclaredField("memberValues");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(invocationHandler);
        } catch (Exception e) {
            throw new RuntimeException("Get annotation member values failed");
        }
    }

    public static boolean isDefaultValue(
        Method method,
        Map<String, Object> memberValues
    ) {
        return isDefaultValue(method, memberValues.get(method.getName()));
    }

    public static boolean isDefaultValue(Method method, Object value) {
        Object defaultValue = method.getDefaultValue();
        if (method.getReturnType().isArray()) {
            return Arrays.equals((Object[]) defaultValue, (Object[]) value);
        } else {
            return defaultValue.equals(value);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List sortByOrderAnnotation(Set classes) {
        List list = new ArrayList(classes);
        list.sort(ORDER_ANNOTATION_COMPARATOR);
        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List sortByOrderAnnotation(List classes) {
        classes.sort(ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    public static Class<?>[] sortByOrderAnnotation(Class<?>[] classes) {
        Arrays.sort(classes, ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    public static Method[] sortByOrderAnnotation(Method[] classes) {
        Arrays.sort(classes, ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    public static List<Class<?>> getTypesAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return ReflectionsUtils.getTypesAnnotatedWith(annotation);
    }

    public static List<Method> getMethodsAnnotated(
        Class<? extends Annotation> annotation
    ) {
        return ReflectionsUtils.getMethodsAnnotatedWith(annotation);
    }

    @SuppressWarnings("unchecked")
    public static List<Class<?>> getTypesAnnotatedAndInherit(
        Class<? extends Annotation> annotation
    ) {
        List<Class<?>> list = getTypesAnnotated(annotation);
        for (Class<?> item : list) {
            if (item.isAnnotation()) {
                list.addAll(
                    getTypesAnnotatedAndInherit(
                        (Class<? extends Annotation>) item
                    )
                );
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Method> getMethodAnnotatedAndInherit(
        Class<? extends Annotation> annotation
    ) {
        List<Method> list = getMethodsAnnotated(annotation);
        for (Class<?> item : getTypesAnnotatedAndInherit(annotation)) {
            if (item.isAnnotation()) {
                list.addAll(
                    getMethodAnnotatedAndInherit(
                        (Class<? extends Annotation>) item
                    )
                );
            }
        }
        return list;
    }
}
