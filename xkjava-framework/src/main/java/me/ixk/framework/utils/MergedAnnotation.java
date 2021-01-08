/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组合注解
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:04
 */
public interface MergedAnnotation {
    String VALUE = "value";
    Logger log = LoggerFactory.getLogger(MergedAnnotation.class);

    /**
     * 获取所有注解
     *
     * @return 注解 Map
     */
    Map<Class<? extends Annotation>, List<Annotation>> annotations();

    /**
     * 获取注解
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解
     */
    default <A extends Annotation> A getAnnotation(
        final Class<A> annotationType
    ) {
        return this.getAnnotation(annotationType, 0);
    }

    /**
     * 获取注解
     *
     * @param annotationType 注解类型
     * @param index          索引
     * @param <A>            注解类型
     * @return 注解
     */
    @SuppressWarnings("unchecked")
    default <A extends Annotation> A getAnnotation(
        final Class<A> annotationType,
        final int index
    ) {
        final List<Annotation> annotations =
            this.annotations().get(annotationType);
        if (
            annotations == null ||
            annotations.isEmpty() ||
            index < 0 ||
            index >= annotations.size()
        ) {
            return null;
        }
        if (annotations.size() > 1) {
            log.warn(
                "Annotation [{}] is multi, but only get one",
                annotationType.getName()
            );
        }
        return (A) annotations.get(index);
    }

    /**
     * 获取指定类型的注解列表
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解列表
     */
    @SuppressWarnings("unchecked")
    default <A extends Annotation> List<A> getAnnotations(
        Class<A> annotationType
    ) {
        return (List<A>) this.annotations()
            .getOrDefault(annotationType, Collections.emptyList());
    }

    /**
     * 是否存在注解
     *
     * @param annotationType 注解类型
     * @return 是否存在
     */
    default boolean hasAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        return this.annotations().containsKey(annotationType);
    }

    /**
     * 是否不存在注解
     *
     * @param annotationType 注解类型
     * @return 是否不存在
     */
    default boolean notAnnotation(Class<? extends Annotation> annotationType) {
        return !this.hasAnnotation(annotationType);
    }

    /**
     * 是否包含多个相同类型的注解
     *
     * @param annotationType 注解类型
     * @return 是否包含
     */
    default boolean hasMultiAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        return (
            this.annotations().containsKey(annotationType) &&
            this.annotations().get(annotationType).size() != 1
        );
    }

    /**
     * 添加注解
     *
     * @param annotation 注解
     */
    default void addAnnotation(Annotation annotation) {
        throw new UnsupportedOperationException(
            "Unsupported add annotation to merge annotation"
        );
    }

    /**
     * 删除注解
     *
     * @param annotationType 注解类型
     */
    default void removeAnnotation(Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException(
            "Unsupported remove annotation to merge annotation"
        );
    }

    /**
     * 删除注解
     *
     * @param annotationType 注解类型
     * @param index          索引
     */
    default void removeAnnotation(
        Class<? extends Annotation> annotationType,
        int index
    ) {
        throw new UnsupportedOperationException(
            "Unsupported add annotation to merge annotation"
        );
    }

    /**
     * 获取注解值
     *
     * @param annotationType 注解类型
     * @return 注解值
     */
    default Object get(Class<? extends Annotation> annotationType) {
        return this.get(annotationType, VALUE);
    }

    /**
     * 获取注解值
     *
     * @param annotationType 注解类型
     * @param name           方法名称
     * @return 注解值
     */
    default Object get(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, Object.class);
    }

    /**
     * 获取注解值
     *
     * @param annotationType 注解类型
     * @return 注解值
     */
    default <T> T get(
        Class<? extends Annotation> annotationType,
        Class<T> returnType
    ) {
        return this.get(annotationType, VALUE, returnType);
    }

    /**
     * 获取注解值
     *
     * @param annotationType 注解类型
     * @param name           方法名称
     * @param returnType     返回类型
     * @param <T>            注解值类型
     * @return 注解值
     */
    default <T> T get(
        Class<? extends Annotation> annotationType,
        String name,
        Class<T> returnType
    ) {
        return this.get(annotationType, name, returnType, 0);
    }

    /**
     * 获取注解值
     *
     * @param annotationType 注解类型
     * @param name           方法名称
     * @param returnType     返回类型
     * @param index          索引
     * @param <T>            注解值类型
     * @return 注解值
     */
    default <T> T get(
        Class<? extends Annotation> annotationType,
        String name,
        Class<T> returnType,
        int index
    ) {
        final Annotation annotation = this.getAnnotation(annotationType, index);
        final Method method = ReflectUtil.getMethodByName(annotationType, name);
        if (annotation == null || method == null) {
            return null;
        }
        return Convert.convert(
            returnType,
            ReflectUtil.invoke(annotation, method)
        );
    }

    default byte getByte(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, byte.class);
    }

    default byte[] getByteArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, byte[].class);
    }

    default boolean getBoolean(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, boolean.class);
    }

    default boolean[] getBooleanArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, boolean[].class);
    }

    default char getChar(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, char.class);
    }

    default char[] getCharArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, char[].class);
    }

    default short getShort(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, short.class);
    }

    default short[] getShortArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, short[].class);
    }

    default int getInt(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, int.class);
    }

    default int[] getIntArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, int[].class);
    }

    default long getLong(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, long.class);
    }

    default long[] getLongArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, long[].class);
    }

    default double getDouble(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, double.class);
    }

    default double[] getDoubleArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, double[].class);
    }

    default float getFloat(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, float.class);
    }

    default float[] getFloatArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, float[].class);
    }

    default String getString(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, String.class);
    }

    default String[] getStringArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, String[].class);
    }

    default Class<?> getClass(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, Class.class);
    }

    default Class<?>[] getClassArray(
        Class<? extends Annotation> annotationType,
        String name
    ) {
        return this.get(annotationType, name, Class[].class);
    }

    default <E extends Enum<E>> E getEnum(
        Class<? extends Annotation> annotationType,
        String name,
        Class<E> type
    ) {
        return this.get(annotationType, name, type);
    }

    static MergedAnnotation from(AnnotatedElement element) {
        return new MergedAnnotationImpl(element);
    }

    static MergedAnnotation wrap(final Annotation annotation) {
        return new MergedAnnotationImpl(annotation);
    }

    static boolean has(
        AnnotatedElement element,
        Class<? extends Annotation> annotationType
    ) {
        return from(element).getAnnotation(annotationType) != null;
    }
}
