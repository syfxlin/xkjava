/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.lang.SimpleCache;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Conditional;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RepeatItem;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;

public abstract class AnnotationUtils {
    private static final SimpleCache<Class<? extends Annotation>, Set<Class<?>>> CLASS_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, Set<Method>> METHOD_ANNOTATION_CACHE = new SimpleCache<>();

    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Integer or1 = getAnnotation((AnnotatedElement) o1)
            .get(Order.class, "order");
        Integer or2 = getAnnotation((AnnotatedElement) o2)
            .get(Order.class, "order");
        int i1 = or1 == null ? Order.MEDIUM_PRECEDENCE : or1;
        int i2 = or2 == null ? Order.MEDIUM_PRECEDENCE : or2;
        return i1 - i2;
    };

    public static MergedAnnotation getAnnotation(
        final AnnotatedElement element
    ) {
        return new MergedAnnotationImpl(element);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Set filterConditionAnnotation(final Collection classes) {
        return (Set) classes
            .stream()
            .filter(clazz -> isCondition((AnnotatedElement) clazz))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SuppressWarnings("unchecked")
    public static Set<Class<?>> getTypesAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Class<?>> cache = CLASS_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return CLASS_ANNOTATION_CACHE.put(
            annotation,
            (Set<Class<?>>) filterConditionAnnotation(
                sortByOrderAnnotation(getTypesAnnotatedWith(annotation))
            )
        );
    }

    @SuppressWarnings("unchecked")
    public static Set<Method> getMethodsAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Method> cache = METHOD_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return METHOD_ANNOTATION_CACHE.put(
            annotation,
            (Set<Method>) filterConditionAnnotation(
                sortByOrderAnnotation(getMethodsAnnotatedWith(annotation))
            )
        );
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<?>> getTypesAnnotatedWith(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Class<?>> set = new LinkedHashSet<>();
        for (final Class<?> item : ReflectionsUtils
            .make()
            .getTypesAnnotatedWith(annotation)) {
            if (item.isAnnotation()) {
                set.addAll(
                    getTypesAnnotatedWith((Class<? extends Annotation>) item)
                );
            } else {
                set.add(item);
            }
        }
        Class<? extends Annotation> repeatable = getRepeatable(annotation);
        if (repeatable != null) {
            set.addAll(getTypesAnnotatedWith(repeatable));
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    private static Set<Method> getMethodsAnnotatedWith(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Method> set = ReflectionsUtils
            .make()
            .getMethodsAnnotatedWith(annotation);
        for (final Class<?> item : ReflectionsUtils
            .make()
            .getTypesAnnotatedWith(annotation)) {
            if (item.isAnnotation()) {
                set.addAll(
                    getMethodsAnnotatedWith((Class<? extends Annotation>) item)
                );
            }
        }
        return set;
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

    @SuppressWarnings("unchecked")
    public static boolean isCondition(final AnnotatedElement element) {
        final MergedAnnotation annotation = getAnnotation(element);
        if (annotation.notAnnotation(Conditional.class)) {
            return true;
        }
        for (final Class<? extends Condition> condition : (Class<? extends Condition>[]) annotation.get(
            Conditional.class,
            "value"
        )) {
            final boolean matches = ReflectUtil.invoke(
                ReflectUtil.newInstance(condition),
                "matches",
                XkJava.of(),
                element,
                annotation
            );
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    public static Class<? extends Annotation> getRepeatItem(
        Class<? extends Annotation> annotationType
    ) {
        RepeatItem repeatItem = annotationType.getAnnotation(RepeatItem.class);
        if (repeatItem == null) {
            return null;
        }
        return repeatItem.value();
    }

    public static Class<? extends Annotation> getRepeatable(
        Class<? extends Annotation> annotationType
    ) {
        Repeatable repeatable = annotationType.getAnnotation(Repeatable.class);
        if (repeatable == null) {
            return null;
        }
        return repeatable.value();
    }

    public static Map<Class<? extends Annotation>, List<Annotation>> mergeAnnotation(
        AnnotatedElement element
    ) {
        Map<Class<? extends Annotation>, List<Annotation>> map = new LinkedHashMap<>();
        mergeAnnotation(element, map);
        return map;
    }

    private static void mergeAnnotation(
        AnnotatedElement element,
        Map<Class<? extends Annotation>, List<Annotation>> map
    ) {
        for (Annotation annotation : element.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            // Add current to map
            List<Annotation> annotationList = map.getOrDefault(
                annotationType,
                new ArrayList<>()
            );
            for (Annotation item : element.getAnnotationsByType(
                annotationType
            )) {
                annotationList.add(mergeAnnotationValue(item));
            }
            map.put(annotationType, annotationList);
            Class<? extends Annotation> repeatItem = getRepeatItem(
                annotationType
            );
            if (repeatItem != null) {
                List<Annotation> itemList = map.getOrDefault(
                    repeatItem,
                    new ArrayList<>()
                );
                for (Annotation item : (Annotation[]) ReflectUtil.invoke(
                    annotation,
                    "value"
                )) {
                    itemList.add(mergeAnnotationValue(item));
                }
                map.put(repeatItem, itemList);
            }
            if (isJdkAnnotation(annotationType)) {
                continue;
            }
            mergeAnnotation(annotationType, map);
        }
    }

    public static <T extends Annotation> T mergeAnnotationValue(
        final T annotation
    ) {
        if (annotation == null) {
            return null;
        }
        final Class<? extends Annotation> annotationClass = annotation.annotationType();
        final Map<String, Object> memberValues = getMemberValues(annotation);
        final Method[] methodValues = annotationClass.getDeclaredMethods();
        // Set current annotation alias
        for (final Method method : methodValues) {
            if (method.getAnnotation(AliasFor.class) == null) {
                continue;
            }
            final String name = method.getName();
            final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
            if (isDefaultValue(method, memberValues)) {
                final String alias = aliasFor.value();
                if (!aliasFor.value().isEmpty()) {
                    memberValues.put(name, memberValues.get(alias));
                }
            }
        }
        // Set to parent annotation alias
        for (final Method method : methodValues) {
            if (method.getAnnotation(AliasFor.class) == null) {
                continue;
            }
            final String name = method.getName();
            final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
            if (aliasFor.annotation() != Annotation.class) {
                Annotation parent = annotationClass.getAnnotation(
                    aliasFor.annotation()
                );
                Map<String, Object> parentMemberValues = getMemberValues(
                    parent
                );
                parentMemberValues.put(
                    aliasFor.attribute().isEmpty()
                        ? name
                        : aliasFor.attribute(),
                    memberValues.get(name)
                );
                // Set parent annotation alias
                mergeAnnotationValue(parent);
            }
        }
        return annotation;
    }

    public static boolean hasAnnotation(
        AnnotatedElement element,
        Class<? extends Annotation> annotationType
    ) {
        return getAnnotation(element).hasAnnotation(annotationType);
    }
}
