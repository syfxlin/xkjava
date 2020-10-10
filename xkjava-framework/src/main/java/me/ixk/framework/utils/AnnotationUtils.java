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
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;

public abstract class AnnotationUtils {
    private static final SimpleCache<Class<? extends Annotation>, Set<Class<?>>> CLASS_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, Set<Method>> METHOD_ANNOTATION_CACHE = new SimpleCache<>();

    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        MergeAnnotation or1 = getAnnotation((AnnotatedElement) o1, Order.class);
        MergeAnnotation or2 = getAnnotation((AnnotatedElement) o2, Order.class);
        int i1 = or1 == null ? Order.MEDIUM_PRECEDENCE : or1.get("order");
        int i2 = or2 == null ? Order.MEDIUM_PRECEDENCE : or2.get("order");
        return i1 - i2;
    };

    public static MergeAnnotation getAnnotation(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        return walkAnnotation(element, annotationType);
    }

    public static MergeAnnotation wrapAnnotation(final Annotation annotation) {
        return new MergeAnnotationWrap(annotation);
    }

    public static MergeAnnotation cloneAnnotation(
        final MergeAnnotation annotation
    ) {
        MergeAnnotation result = new MergeAnnotationList();
        for (Class<? extends Annotation> value : annotation.indexes()) {
            result.addAnnotation(annotation.getAnnotation(value));
        }
        return result;
    }

    public static <T> T getAnnotationValue(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType,
        final String name
    ) {
        return getAnnotationValue(
            getAnnotation(element, annotationType),
            name,
            annotationType
        );
    }

    public static <T> T getAnnotationValue(
        final MergeAnnotation annotations,
        final String name
    ) {
        return getAnnotationValue(annotations, name, null);
    }

    public static <T> T getAnnotationValue(
        final MergeAnnotation annotations,
        final String name,
        final Class<? extends Annotation> annotationType
    ) {
        if (annotations == null) {
            return null;
        }
        for (int i = annotations.size() - 1; i >= 0; i--) {
            final Annotation annotation = annotations.getAnnotation(i);
            final Class<? extends Annotation> type = annotation.annotationType();
            if (annotationType != null && type != annotationType) {
                continue;
            }
            final Method method = ReflectUtil.getMethod(type, name);
            if (method == null) {
                continue;
            }
            return ReflectUtil.invoke(annotation, method);
        }
        return null;
    }

    public static <T> T getAnnotationValue(
        final MergeAnnotation annotations,
        final Class<T> returnType,
        final String name
    ) {
        if (annotations == null) {
            return null;
        }
        for (int i = annotations.size() - 1; i >= 0; i--) {
            final Annotation annotation = annotations.getAnnotation(i);
            final Class<? extends Annotation> type = annotation.annotationType();
            final Method method = ReflectUtil.getMethod(type, name);
            if (method == null || method.getReturnType() != returnType) {
                continue;
            }
            return ReflectUtil.invoke(annotation, method);
        }
        return null;
    }

    public static MultiValueMap<String, Object> getAnnotationValues(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        final MergeAnnotation annotations = walkAnnotation(
            element,
            annotationType
        );
        if (annotations == null) {
            return null;
        }
        final MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
        for (int i = annotations.size() - 1; i >= 0; i--) {
            final Annotation annotation = annotations.getAnnotation(i);
            final Class<? extends Annotation> type = annotation.annotationType();
            for (final Method method : type.getDeclaredMethods()) {
                result.add(
                    method.getName(),
                    ReflectUtil.invoke(annotation, method)
                );
            }
        }
        return result;
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
                    final Object object = ReflectUtil.invoke(
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

    public static MergeAnnotationList walkAnnotation(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        final Annotation annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (final Annotation item : element.getAnnotations()) {
                final Class<? extends Annotation> annotationClass = item.annotationType();
                if (isJdkAnnotation(annotationClass)) {
                    continue;
                }
                final MergeAnnotationList typeAnnotation = walkAnnotation(
                    annotationClass,
                    annotationType
                );
                if (typeAnnotation != null) {
                    typeAnnotation.addAnnotation(item);
                    return typeAnnotation;
                }
            }
        }
        if (annotation != null) {
            final MergeAnnotationList walkAnnotation = new MergeAnnotationList();
            walkAnnotation.addAnnotation(annotation);
            return walkAnnotation;
        }
        return null;
    }

    public static boolean hasAnnotation(
        final AnnotatedElement element,
        final Class<? extends Annotation> annotationType
    ) {
        final Annotation annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (final Annotation item : element.getAnnotations()) {
                final Class<? extends Annotation> annotationClass = item.annotationType();
                if (isJdkAnnotation(annotationClass)) {
                    continue;
                }
                final boolean hasAnnotation = hasAnnotation(
                    annotationClass,
                    annotationType
                );
                if (hasAnnotation) {
                    return true;
                }
            }
        }
        return annotation != null;
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
        final MergeAnnotation annotation = getAnnotation(
            element,
            Conditional.class
        );
        if (annotation == null) {
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

    private static class MergeAnnotationList implements MergeAnnotation {
        private final List<Class<? extends Annotation>> indexes = new ArrayList<>();
        private final Map<Class<? extends Annotation>, Annotation> annotations = new LinkedHashMap<>();

        public void addAnnotation(final Annotation annotation) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            this.indexes.add(annotationType);
            this.annotations.put(annotationType, parseAnnotation(annotation));
        }

        @Override
        public void removeAnnotation(
            Class<? extends Annotation> annotationType
        ) {
            if (annotationType == null) {
                return;
            }
            this.indexes.remove(annotationType);
            this.annotations.remove(annotationType);
        }

        @Override
        public void removeAnnotation(int index) {
            this.removeAnnotation(this.getAnnotation(index));
        }

        @Override
        public Map<Class<? extends Annotation>, Annotation> annotations() {
            return this.annotations;
        }

        @Override
        public List<Class<? extends Annotation>> indexes() {
            return this.indexes;
        }
    }

    private static class MergeAnnotationWrap implements MergeAnnotation {
        private final List<Class<? extends Annotation>> indexes = new ArrayList<>();
        private final Map<Class<? extends Annotation>, Annotation> annotations = new LinkedHashMap<>();

        public MergeAnnotationWrap(Annotation annotation) {
            this.indexes.add(annotation.annotationType());
            this.annotations.put(
                    annotation.annotationType(),
                    parseAnnotation(annotation)
                );
        }

        @Override
        public Map<Class<? extends Annotation>, Annotation> annotations() {
            return this.annotations;
        }

        @Override
        public List<Class<? extends Annotation>> indexes() {
            return this.indexes;
        }
    }
}
