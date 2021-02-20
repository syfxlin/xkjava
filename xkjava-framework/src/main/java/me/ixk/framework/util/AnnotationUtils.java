/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

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
import me.ixk.framework.annotation.AliasFor;
import me.ixk.framework.annotation.Order;
import me.ixk.framework.annotation.RepeatItem;

/**
 * 注解工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:56
 */
public class AnnotationUtils {

    private static final SoftCache<AnnotatedElement, Map<Class<? extends Annotation>, List<Annotation>>> MERGED_ANNOTATION_CACHE = new SoftCache<>();

    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Integer or1 = MergedAnnotation
            .from((AnnotatedElement) o1)
            .get(Order.class, "order", Integer.class);
        Integer or2 = MergedAnnotation
            .from((AnnotatedElement) o2)
            .get(Order.class, "order", Integer.class);
        int i1 = or1 == null ? Order.MEDIUM_PRECEDENCE : or1;
        int i2 = or2 == null ? Order.MEDIUM_PRECEDENCE : or2;
        return Integer.compare(i1, i2);
    };

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

    private static Map<Class<? extends Annotation>, List<Annotation>> annotationForMergeAnnotation(
        Map<Class<? extends Annotation>, List<Map<String, Object>>> annotationMap
    ) {
        return annotationMap
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    e ->
                        e
                            .getValue()
                            .stream()
                            .map(
                                i ->
                                    AnnotationUtils.annotationForMap(
                                        e.getKey(),
                                        i
                                    )
                            )
                            .collect(Collectors.toList()),
                    (u, v) -> {
                        throw new IllegalStateException(
                            String.format("Duplicate key %s", u)
                        );
                    },
                    LinkedHashMap::new
                )
            );
    }

    public static Map<Class<? extends Annotation>, List<Annotation>> mergeAnnotation(
        final Annotation annotation
    ) {
        final Map<Class<? extends Annotation>, List<Map<String, Object>>> annotationMap = new LinkedHashMap<>();
        final HashMap<Class<? extends Annotation>, Map<String, Object>> overwriteMap = new HashMap<>();
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        annotationMap.put(
            annotationType,
            Collections.singletonList(
                mergeAnnotationValue(annotation, overwriteMap)
            )
        );
        walkAnnotation(annotationType, annotationMap, overwriteMap);
        return annotationForMergeAnnotation(annotationMap);
    }

    public static Map<Class<? extends Annotation>, List<Annotation>> mergeAnnotation(
        final AnnotatedElement element
    ) {
        return MERGED_ANNOTATION_CACHE.computeIfAbsent(
            element,
            k -> {
                final Map<Class<? extends Annotation>, List<Map<String, Object>>> annotationMap = new LinkedHashMap<>();
                walkAnnotation(k, annotationMap, new HashMap<>());
                return annotationForMergeAnnotation(annotationMap);
            }
        );
    }

    private static void walkAnnotation(
        final AnnotatedElement element,
        Map<Class<? extends Annotation>, List<Map<String, Object>>> annotationMap,
        Map<Class<? extends Annotation>, Map<String, Object>> overwriteMap
    ) {
        for (Annotation annotation : element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isJdkAnnotation(annotationType)) {
                continue;
            }
            // 处理当前注解
            final List<Map<String, Object>> annotations = annotationMap.getOrDefault(
                annotationType,
                new ArrayList<>()
            );
            for (Annotation item : element.getAnnotationsByType(
                annotationType
            )) {
                annotations.add(mergeAnnotationValue(item, overwriteMap));
            }
            annotationMap.put(annotationType, annotations);
            // 处理重复注解
            final Class<? extends Annotation> repeatItem = getRepeatItem(
                annotationType
            );
            if (repeatItem != null) {
                final List<Map<String, Object>> itemList = annotationMap.getOrDefault(
                    repeatItem,
                    new ArrayList<>()
                );
                for (final Annotation item : (Annotation[]) ReflectUtil.invoke(
                    annotation,
                    "value"
                )) {
                    itemList.add(mergeAnnotationValue(item, overwriteMap));
                }
                annotationMap.put(repeatItem, itemList);
            }
            // 处理父注解
            walkAnnotation(annotationType, annotationMap, overwriteMap);
        }
    }

    private static Map<String, Object> mergeAnnotationValue(
        final Annotation annotation,
        final Map<Class<? extends Annotation>, Map<String, Object>> overwriteMap
    ) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final Map<String, Object> overwrite = overwriteMap.get(annotationType);
        // 原始 memberValues，不可修改，因为这是单例的
        final Map<String, Object> memberValues = getMemberValues(annotation);
        // 实际复制并操作后的 memberValues
        final Map<String, Object> values = new HashMap<>(memberValues.size());
        for (Entry<String, Object> entry : memberValues.entrySet()) {
            final String attributeName = entry.getKey();
            Object attributeValue = entry.getValue();
            final Method method = ReflectUtil.getMethod(
                annotationType,
                attributeName
            );
            final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
            if (overwrite != null && overwrite.containsKey(attributeName)) {
                // 如果从子元素设置了重写的值，那么就设置该值
                attributeValue = overwrite.get(attributeName);
            } else if (
                aliasFor != null &&
                !aliasFor.value().isEmpty() &&
                isDefaultValue(method, memberValues)
            ) {
                // 如果为默认值，同时设置了 AliasFor.value 那么就使用别名的值（即使是默认值也一样）
                final String alias = aliasFor.value();
                if (overwrite != null && overwrite.containsKey(alias)) {
                    attributeValue = overwrite.get(alias);
                } else {
                    attributeValue = memberValues.get(alias);
                }
            }
            // 否则把自身 memberValues 值设置到新 Map 中
            values.put(attributeName, attributeValue);
            // 如果设置了 AliasFor.annotation 那么就设置父注解的重写值
            if (aliasFor != null && aliasFor.annotation() != Annotation.class) {
                final Class<? extends Annotation> parentType = aliasFor.annotation();
                final Map<String, Object> parentOverwrite = overwriteMap.getOrDefault(
                    parentType,
                    new HashMap<>()
                );
                parentOverwrite.put(
                    aliasFor.attribute().isEmpty()
                        ? attributeName
                        : aliasFor.attribute(),
                    attributeValue
                );
                overwriteMap.put(parentType, parentOverwrite);
            }
        }
        return values;
    }
}
