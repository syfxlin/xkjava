/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.util.ClassUtil;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.annotations.Order;

public abstract class AnnotationUtils {
    private static final Comparator<Object> ORDER_ANNOTATION_COMPARATOR = (o1, o2) -> {
        Order or1 = null, or2 = null;
        if (o1 instanceof Class && o2 instanceof Class) {
            or1 = getAnnotation((Class<?>) o1, Order.class);
            or2 = getAnnotation((Class<?>) o2, Order.class);
        } else if (o1 instanceof Method && o2 instanceof Method) {
            or1 = getAnnotation((Method) o1, Order.class);
            or2 = getAnnotation((Method) o1, Order.class);
        }
        int i1 = or1 == null ? Order.LOWEST_PRECEDENCE : or1.order();
        int i2 = or2 == null ? Order.LOWEST_PRECEDENCE : or2.order();
        return i1 - i2;
    };

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(
        final AnnotatedElement element,
        final Class<T> annotationType
    ) {
        final WalkAnnotation annotation = walkAnnotation(
            element,
            annotationType
        );
        if (annotation == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(
            AnnotationUtils.class.getClassLoader(),
            new Class[] { annotationType, AnnotationObject.class },
            new AnnotationInvocationHandler(annotation)
        );
    }

    public static <T> T getAnnotationValue(
        AnnotatedElement element,
        Class<? extends Annotation> annotationType,
        String name
    ) {
        final WalkAnnotation walkAnnotation = walkAnnotation(
            element,
            annotationType
        );
        if (walkAnnotation == null) {
            return null;
        }
        return getAnnotationValue(
            walkAnnotation.annotations(),
            name,
            annotationType
        );
    }

    public static <T> T getAnnotationValue(
        List<Annotation> annotations,
        String name
    ) {
        return getAnnotationValue(annotations, name, null);
    }

    public static <T> T getAnnotationValue(
        List<Annotation> annotations,
        String name,
        Class<? extends Annotation> annotationType
    ) {
        if (annotations == null) {
            return null;
        }
        for (int i = annotations.size() - 1; i >= 0; i--) {
            Annotation annotation = annotations.get(i);
            Class<? extends Annotation> type = annotation.annotationType();
            if (annotationType != null && type != annotationType) {
                continue;
            }
            Method method = ReflectUtil.getMethod(type, name);
            if (method == null) {
                continue;
            }
            return ReflectUtil.invoke(annotation, method);
        }
        return null;
    }

    public static <T> T getAnnotationValue(
        List<Annotation> annotations,
        Class<T> returnType,
        String name
    ) {
        if (annotations == null) {
            return null;
        }
        for (int i = annotations.size() - 1; i >= 0; i--) {
            Annotation annotation = annotations.get(i);
            Class<? extends Annotation> type = annotation.annotationType();
            Method method = ReflectUtil.getMethod(type, name);
            if (method == null || method.getReturnType() != returnType) {
                continue;
            }
            return ReflectUtil.invoke(annotation, method);
        }
        return null;
    }

    public static MultiValueMap<String, Object> getAnnotationValues(
        AnnotatedElement element,
        Class<? extends Annotation> annotationType
    ) {
        WalkAnnotation walkAnnotation = walkAnnotation(element, annotationType);
        if (walkAnnotation == null) {
            return null;
        }
        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
        List<Annotation> annotations = walkAnnotation.annotations();
        for (int i = annotations.size() - 1; i >= 0; i--) {
            Annotation annotation = annotations.get(i);
            Class<? extends Annotation> type = annotation.annotationType();
            for (Method method : type.getDeclaredMethods()) {
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

    public static WalkAnnotation walkAnnotation(
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
                final WalkAnnotation typeAnnotation = walkAnnotation(
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
            final WalkAnnotation walkAnnotation = new WalkAnnotation();
            walkAnnotation.addAnnotation(annotation);
            return walkAnnotation;
        }
        return null;
    }

    public static boolean hasAnnotation(
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

    private static class WalkAnnotation implements AnnotationObject {
        private final List<Annotation> annotations = new LinkedList<>();

        public WalkAnnotation() {}

        public void addAnnotation(Annotation annotation) {
            this.annotations.add(parseAnnotation(annotation));
        }

        @Override
        public List<Annotation> annotations() {
            return this.annotations;
        }
    }

    private static class AnnotationInvocationHandler
        implements InvocationHandler {
        private final WalkAnnotation annotation;

        public AnnotationInvocationHandler(final WalkAnnotation annotation) {
            this.annotation = annotation;
        }

        @Override
        public Object invoke(
            final Object proxy,
            final Method method,
            final Object[] args
        )
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
                case "getParent":
                    if (this.annotation == null) {
                        return null;
                    }
                    return this.annotation.getParent();
                case "getTarget":
                    if (this.annotation == null) {
                        return null;
                    }
                    return this.annotation.getTarget();
                case "annotations":
                    if (this.annotation == null) {
                        return null;
                    }
                    return this.annotation.annotations();
                case "get":
                    if (this.annotation == null) {
                        return null;
                    }
                    if (args.length == 0) {
                        return this.annotation.get(
                                "get",
                                method.getReturnType()
                            );
                    }
                    return ReflectUtil.invoke(
                        this.annotation,
                        this.annotation.getClass()
                            .getMethod("get", ClassUtil.getClasses(args)),
                        args
                    );
                default:
                    if (this.annotation == null) {
                        return null;
                    }
                    return this.annotation.get(
                            method.getName(),
                            method.getReturnType()
                        );
            }
        }
    }

    public interface AnnotationObject {
        List<Annotation> annotations();

        default Annotation getTarget() {
            List<Annotation> list = this.annotations();
            return list.isEmpty() ? null : list.get(0);
        }

        default Annotation getParent() {
            List<Annotation> list = this.annotations();
            return list.isEmpty() ? null : list.get(list.size() - 1);
        }

        default Object get(final String key) {
            return getAnnotationValue(this.annotations(), key);
        }

        default <T> T get(final String key, final Class<T> returnType) {
            return getAnnotationValue(this.annotations(), returnType, key);
        }

        default Object get(
            final Class<? extends Annotation> annotationType,
            final String key
        ) {
            return getAnnotationValue(this.annotations(), key, annotationType);
        }
    }
}
