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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Conditional;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.RepeatItem;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;

/**
 * 注解工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:56
 */
public class AnnotationUtils {
    private static final SimpleCache<AnnotatedElement, Map<Class<? extends Annotation>, List<Annotation>>> MERGED_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, Set<Class<?>>> CLASS_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, Set<Method>> METHOD_ANNOTATION_CACHE = new SimpleCache<>();

    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Integer or1 = getAnnotation((AnnotatedElement) o1)
            .get(Order.class, "order");
        Integer or2 = getAnnotation((AnnotatedElement) o2)
            .get(Order.class, "order");
        int i1 = or1 == null ? Order.MEDIUM_PRECEDENCE : or1;
        int i2 = or2 == null ? Order.MEDIUM_PRECEDENCE : or2;
        return Integer.compare(i1, i2);
    };

    public static <A extends Annotation> A getAnnotation(
        final AnnotatedElement element,
        final Class<A> annotationType
    ) {
        return getAnnotation(element).getAnnotation(annotationType);
    }

    public static MergedAnnotation getAnnotation(
        final AnnotatedElement element
    ) {
        return new MergedAnnotationImpl(element);
    }

    public static MergedAnnotation wrapAnnotation(final Annotation annotation) {
        final MergedAnnotationImpl mergedAnnotation = new MergedAnnotationImpl();
        mergedAnnotation
            .annotations()
            .put(
                annotation.annotationType(),
                Collections.singletonList(annotation)
            );
        return mergedAnnotation;
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

    private static Map<String, Object> getAndCloneMemberValues(
        final Annotation annotation
    ) {
        return new HashMap<>(getMemberValues(annotation));
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
        final Class<? extends Annotation> repeatable = getRepeatable(
            annotation
        );
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
        final Class<? extends Annotation> annotationType
    ) {
        final RepeatItem repeatItem = annotationType.getAnnotation(
            RepeatItem.class
        );
        if (repeatItem == null) {
            return null;
        }
        return repeatItem.value();
    }

    public static Class<? extends Annotation> getRepeatable(
        final Class<? extends Annotation> annotationType
    ) {
        final Repeatable repeatable = annotationType.getAnnotation(
            Repeatable.class
        );
        if (repeatable == null) {
            return null;
        }
        return repeatable.value();
    }

    public static boolean hasAnnotation(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        return getAnnotation(element).hasAnnotation(annotationType);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A annotationForMap(
        final Class<A> annotationType,
        final Map<String, Object> memberValues
    ) {
        return (A) Proxy.newProxyInstance(
            annotationType.getClassLoader(),
            new Class[] { annotationType },
            new AnnotationInvocationHandler(annotationType, memberValues)
        );
    }

    private static void walkAnnotation(
        final AnnotatedElement element,
        Map<Class<? extends Annotation>, List<Map<String, Object>>> map
    ) {
        for (Annotation annotation : element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isJdkAnnotation(annotationType)) {
                continue;
            }
            final List<Map<String, Object>> annotationList = map.getOrDefault(
                annotationType,
                new ArrayList<>()
            );
            for (final Annotation item : element.getAnnotationsByType(
                annotationType
            )) {
                annotationList.add(getAndCloneMemberValues(item));
            }
            map.put(annotationType, annotationList);
            final Class<? extends Annotation> repeatItem = getRepeatItem(
                annotationType
            );
            if (repeatItem != null) {
                final List<Map<String, Object>> itemList = map.getOrDefault(
                    repeatItem,
                    new ArrayList<>()
                );
                for (final Annotation item : (Annotation[]) ReflectUtil.invoke(
                    annotation,
                    "value"
                )) {
                    itemList.add(getAndCloneMemberValues(item));
                }
                map.put(repeatItem, itemList);
            }
            walkAnnotation(annotationType, map);
        }
    }

    private static void mergeAnnotation(
        final AnnotatedElement element,
        final Map<Class<? extends Annotation>, List<Map<String, Object>>> map
    ) {
        for (Annotation annotation : element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isJdkAnnotation(annotationType)) {
                continue;
            }
            mergeAnnotationValue(annotationType, map);
            mergeAnnotation(annotationType, map);
        }
    }

    public static Map<Class<? extends Annotation>, List<Annotation>> mergeAnnotation(
        final AnnotatedElement element
    ) {
        final Map<Class<? extends Annotation>, List<Annotation>> cache = MERGED_ANNOTATION_CACHE.get(
            element
        );
        if (cache != null) {
            return cache;
        }
        final Map<Class<? extends Annotation>, List<Map<String, Object>>> map = new LinkedHashMap<>();
        walkAnnotation(element, map);
        mergeAnnotation(element, map);
        final LinkedHashMap<Class<? extends Annotation>, List<Annotation>> result = map
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    e ->
                        e
                            .getValue()
                            .stream()
                            .map(i -> annotationForMap(e.getKey(), i))
                            .collect(Collectors.toList()),
                    (u, v) -> {
                        throw new IllegalStateException(
                            String.format("Duplicate key %s", u)
                        );
                    },
                    LinkedHashMap::new
                )
            );
        MERGED_ANNOTATION_CACHE.put(element, result);
        return result;
    }

    private static void mergeAnnotationValue(
        final Class<? extends Annotation> annotationType,
        final Map<Class<? extends Annotation>, List<Map<String, Object>>> map
    ) {
        for (final Map<String, Object> memberValues : map.get(annotationType)) {
            final Method[] methods = annotationType.getDeclaredMethods();
            for (final Method method : methods) {
                final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor == null) {
                    continue;
                }
                final String name = method.getName();
                if (isDefaultValue(method, memberValues)) {
                    final String alias = aliasFor.value();
                    if (!aliasFor.value().isEmpty()) {
                        memberValues.put(name, memberValues.get(alias));
                    }
                }
            }
            for (final Method method : methods) {
                final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor == null) {
                    continue;
                }
                final String name = method.getName();
                if (aliasFor.annotation() != Annotation.class) {
                    final List<Map<String, Object>> parentList = map.get(
                        aliasFor.annotation()
                    );
                    if (parentList != null) {
                        for (Map<String, Object> parentMemberValues : parentList) {
                            parentMemberValues.put(
                                aliasFor.attribute().isEmpty()
                                    ? name
                                    : aliasFor.attribute(),
                                memberValues.get(name)
                            );
                        }
                    }
                }
            }
        }
    }
}
