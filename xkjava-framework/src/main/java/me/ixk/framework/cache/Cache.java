/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

/**
 * 缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 1:20
 */
public interface Cache {
    String DEFAULT_KEY = "DEFAULT_KEY";

    /**
     * 获取缓存名称
     *
     * @return 缓存名称
     */
    String getName();

    /**
     * 获取缓存源对象
     *
     * @return 源对象
     */
    Object getNativeCache();

    /**
     * 获取值
     *
     * @param key 键
     *
     * @return 值
     */
    default Object get(final Object key) {
        return get(key, Object.class);
    }

    /**
     * 获取值
     *
     * @param key        键
     * @param returnType 返回值类型
     * @param <T>        返回值类型
     *
     * @return 值
     */
    <T> T get(Object key, Class<T> returnType);

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    void put(Object key, Object value);

    /**
     * 设置值，如果不存在
     *
     * @param key   键
     * @param value 值
     *
     * @return 返回值
     */
    default Object putIfAbsent(final Object key, final Object value) {
        final Object result = get(key);
        if (result == null) {
            put(key, value);
        }
        return result;
    }

    /**
     * 删除值
     *
     * @param key 键
     */
    void evict(Object key);

    /**
     * 立即删除值
     *
     * @param key 键
     *
     * @return 是否删除
     */
    default boolean evictIfPresent(final Object key) {
        evict(key);
        return false;
    }

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 立即清空值
     *
     * @return 是否清空
     */
    default boolean invalidate() {
        clear();
        return false;
    }
}
