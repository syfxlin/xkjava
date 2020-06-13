package me.ixk.framework.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Order;

public abstract class AnnotationUtils extends AnnotationUtil {
    private static final Comparator<Class<?>> ORDER_ANNOTATION_COMPARATOR = (c1, c2) -> {
        Order o1 = getAnnotation(c1, Order.class);
        Order o2 = getAnnotation(c2, Order.class);
        int i1 = o1 == null ? 0 : o1.value();
        int i2 = o2 == null ? 0 : o2.value();
        return i1 - i2;
    };

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
        try {
            return annotation
                .annotationType()
                .getMethod(key)
                .invoke(annotation);
        } catch (Exception e) {
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

    public static List<Class<?>> sortByOrderAnnotation(Set<Class<?>> classes) {
        List<Class<?>> list = new ArrayList<>(classes);
        list.sort(ORDER_ANNOTATION_COMPARATOR);
        return list;
    }

    public static List<Class<?>> sortByOrderAnnotation(List<Class<?>> classes) {
        classes.sort(ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    public static Class<?>[] sortByOrderAnnotation(Class<?>[] classes) {
        Arrays.sort(classes, ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }
}
