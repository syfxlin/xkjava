/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Native;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Order;

public abstract class AnnotationUtils extends AnnotationUtil {
    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Order or1 = null, or2 = null;
        if (o1 instanceof Class && o2 instanceof Class) {
            or1 = getParentAnnotation((Class<?>) o1, Order.class);
            or2 = getParentAnnotation((Class<?>) o2, Order.class);
        } else if (o1 instanceof Method && o2 instanceof Method) {
            or1 = getParentAnnotation((Method) o1, Order.class);
            or2 = getParentAnnotation((Method) o1, Order.class);
        }
        int i1 = or1 == null ? Order.LOWEST_PRECEDENCE : or1.value();
        int i2 = or2 == null ? Order.LOWEST_PRECEDENCE : or2.value();
        return i1 - i2;
    };

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getParentAnnotation(
        final AnnotatedElement annotatedElement,
        final Class<T> annotationType
    ) {
        Annotation annotation = parseAnnotation(
            walkGetParentAnnotation(annotatedElement, annotationType)
        );
        if (annotation == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(
            AnnotationUtils.class.getClassLoader(),
            new Class[] { annotationType },
            new AnnotationInvocationHandler(annotation)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getTargetAnnotation(
        final AnnotatedElement annotatedElement,
        final Class<T> annotationType
    ) {
        Annotation annotation = parseAnnotation(
            walkGetTargetAnnotation(annotatedElement, annotationType)
        );
        if (annotation == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(
            AnnotationUtils.class.getClassLoader(),
            new Class[] { annotationType },
            new AnnotationInvocationHandler(annotation)
        );
    }

    public static <T extends Annotation> T parseAnnotation(final T annotation) {
        if (annotation == null) {
            return null;
        }
        final Class<? extends Annotation> annotationClass = annotation.annotationType();
        final Map<String, Object> memberValues = getMemberValues(annotation);
        final Method[] methodValues = annotationClass.getDeclaredMethods();
        for (final Method method : methodValues) {
            if (method.getAnnotation(AliasFor.class) == null) {
                continue;
            }
            final String name = method.getName();
            if (isDefaultValue(method, memberValues)) {
                final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                final String alias = aliasFor.value();
                if (aliasFor.annotation() != Annotation.class) {
                    final Object object = getAnnotationValue(
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

    public static Object getAnnotationValue(
        final Annotation annotation,
        final String key
    ) {
        if (annotation == null) {
            return null;
        }
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        try {
            return ReflectUtil.invoke(annotation, key);
        } catch (final Exception e) {
            for (final Annotation item : annotationType.getAnnotations()) {
                final Class<? extends Annotation> itemType = item.annotationType();
                if (isJdkAnnotation(itemType)) {
                    continue;
                }
                final Object value = getAnnotationValue(item, key);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }

    public static Object getAnnotationValue(
        final Class<?> type,
        final Class<? extends Annotation> annotationType,
        String key
    ) {
        return getAnnotationValue(
            getParentAnnotation(type, annotationType),
            key
        );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMemberValues(
        final Annotation annotation
    ) {
        try {
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(
                annotation
            );
            final Field field = invocationHandler
                .getClass()
                .getDeclaredField("memberValues");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(invocationHandler);
        } catch (final Exception e) {
            throw new RuntimeException("Get annotation member values failed");
        }
    }

    public static boolean isDefaultValue(
        final Method method,
        final Map<String, Object> memberValues
    ) {
        return isDefaultValue(method, memberValues.get(method.getName()));
    }

    public static boolean isDefaultValue(
        final Method method,
        final Object value
    ) {
        final Object defaultValue = method.getDefaultValue();
        if (method.getReturnType().isArray()) {
            return Arrays.equals((Object[]) defaultValue, (Object[]) value);
        } else {
            return defaultValue.equals(value);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Set sortByOrderAnnotation(final Collection classes) {
        return (Set) classes
            .stream()
            .sorted(ORDER_ANNOTATION_COMPARATOR)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Class<?>[] sortByOrderAnnotation(final Class<?>[] classes) {
        Arrays.sort(classes, ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    public static Method[] sortByOrderAnnotation(final Method[] classes) {
        Arrays.sort(classes, ORDER_ANNOTATION_COMPARATOR);
        return classes;
    }

    @SuppressWarnings("unchecked")
    public static Set<Class<?>> getTypesAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Class<?>> set = new LinkedHashSet<>();
        for (final Class<?> item : ReflectionsUtils.getTypesAnnotatedWith(
            annotation
        )) {
            if (item.isAnnotation()) {
                set.addAll(
                    getTypesAnnotated((Class<? extends Annotation>) item)
                );
            } else {
                set.add(item);
            }
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public static Set<Method> getMethodsAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Method> set = ReflectionsUtils.getMethodsAnnotatedWith(
            annotation
        );
        for (final Class<?> item : ReflectionsUtils.getTypesAnnotatedWith(
            annotation
        )) {
            if (item.isAnnotation()) {
                set.addAll(
                    getMethodsAnnotated((Class<? extends Annotation>) item)
                );
            }
        }
        return set;
    }

    public static <T extends Annotation> T walkGetTargetAnnotation(
        final AnnotatedElement element,
        final Class<T> annotationType
    ) {
        T annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (final Annotation item : element.getAnnotations()) {
                final Class<? extends Annotation> annotationClass = item.annotationType();
                if (isJdkAnnotation(annotationClass)) {
                    continue;
                }
                final T typeAnnotation = walkGetTargetAnnotation(
                    annotationClass,
                    annotationType
                );
                if (typeAnnotation != null) {
                    annotation = typeAnnotation;
                    break;
                }
            }
        }
        return annotation;
    }

    public static Annotation walkGetParentAnnotation(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        Annotation annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (final Annotation item : element.getAnnotations()) {
                final Class<? extends Annotation> annotationClass = item.annotationType();
                if (isJdkAnnotation(annotationClass)) {
                    continue;
                }
                final Annotation typeAnnotation = walkGetParentAnnotation(
                    annotationClass,
                    annotationType
                );
                if (typeAnnotation != null) {
                    return item;
                }
            }
        }
        return annotation;
    }

    public static boolean isJdkAnnotation(
        final Class<? extends Annotation> type
    ) {
        return (
            type == Documented.class ||
            type == Retention.class ||
            type == Inherited.class ||
            type == Native.class ||
            type == Repeatable.class ||
            type == Target.class
        );
    }

    private static class AnnotationInvocationHandler
        implements InvocationHandler {
        private final Annotation annotation;

        public AnnotationInvocationHandler(Annotation annotation) {
            this.annotation = annotation;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
            switch (method.getName()) {
                case "equals":
                    return (proxy == args[0]);
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return (
                        this.annotation == null ? "" : this.annotation
                    ).toString();
                default:
                    if (this.annotation == null) {
                        return null;
                    }
                    return getAnnotationValue(annotation, method.getName());
            }
        }
    }
}
