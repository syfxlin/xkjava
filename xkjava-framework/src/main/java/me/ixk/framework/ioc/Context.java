/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.utils.Convert;

/**
 * Context
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:22
 */
public interface Context {
    /**
     * 别名
     *
     * @return 别名 Map
     */
    Map<String, String> getAliases();

    /**
     * Bindings
     *
     * @return Binding Map
     */
    Map<String, Binding> getBindings();

    /**
     * 属性
     *
     * @return 属性列表
     */
    Map<String, Object> getAttributes();

    /**
     * 获取 Context 名称
     *
     * @return Context 名称
     */
    String getName();

    /**
     * 是否匹配作用域类型
     *
     * @param scopeType 作用域类型
     *
     * @return 是否匹配
     */
    boolean matchesScope(ScopeType scopeType);

    /**
     * 该 Context 是否启动，一般的 Context 只要 new 后就会启动 但是如果是 ThreadLocal 则需要另行启动
     *
     * @return 是否启动
     */
    default boolean isCreated() {
        return true;
    }

    /* ====================== binding ======================= */

    /**
     * 获取 Binding
     *
     * @param name Binding 名称
     *
     * @return Binding
     */
    default Binding getBinding(String name) {
        name = this.getCanonicalName(name);
        return this.getBindings().get(name);
    }

    /**
     * 设置 Binding
     *
     * @param name    Binding 名称
     * @param binding Binding
     *
     * @return Binding
     */
    default Binding setBinding(String name, Binding binding) {
        name = this.getCanonicalName(name);
        this.getBindings().put(name, binding);
        return binding;
    }

    /**
     * 是否存在 Binding
     *
     * @param name Binding 名称
     *
     * @return 是否存在
     */
    default boolean hasBinding(String name) {
        name = this.getCanonicalName(name);
        return this.getBindings().containsKey(name);
    }

    /**
     * 删除 Binding
     *
     * @param name Binding 名称
     */
    default void removeBinding(String name) {
        String canonicalName = name;
        String resolvedName;
        do {
            resolvedName = this.getAlias(canonicalName);
            if (resolvedName != null) {
                this.removeAlias(canonicalName);
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        this.getBindings().remove(canonicalName);
    }

    /**
     * 获取 Binding
     *
     * @param requiredType 实例类型
     *
     * @return Binding
     */
    default Binding getBinding(Class<?> requiredType) {
        return this.getBinding(requiredType.getName());
    }

    /**
     * 设置 Binding
     *
     * @param requiredType 实例类型
     * @param binding      Binding
     *
     * @return Binding
     */
    default Binding setBinding(Class<?> requiredType, Binding binding) {
        return this.setBinding(requiredType.getName(), binding);
    }

    /**
     * 是否存在 Binding
     *
     * @param requiredType 实例类型
     *
     * @return 是否存在
     */
    default boolean hasBinding(Class<?> requiredType) {
        return this.hasBinding(requiredType.getName());
    }

    /**
     * 删除 Binding
     *
     * @param requiredType 实例类型
     */
    default void removeBinding(Class<?> requiredType) {
        this.removeBinding(requiredType.getName());
    }

    /* ====================== alias ======================= */

    /**
     * 注册别名
     *
     * @param alias 别名
     * @param name  实际名称
     */
    default void registerAlias(String alias, String name) {
        this.getAliases().put(alias, name);
    }

    /**
     * 删除别名
     *
     * @param alias 别名
     */
    default void removeAlias(String alias) {
        this.getAliases().remove(alias);
    }

    /**
     * 是否存在别名
     *
     * @param alias 别名
     *
     * @return 是否存在
     */
    default boolean hasAlias(String alias) {
        return this.getAliases().containsKey(alias);
    }

    /**
     * 从别名获取实际名称
     *
     * @param alias 别名
     *
     * @return 实际名称
     */
    default String getAlias(String alias) {
        return this.getAliases().get(alias);
    }

    /**
     * 获取实际名称
     *
     * @param name 别名
     *
     * @return 实际名称
     */
    default String getCanonicalName(String name) {
        String canonicalName = name;
        String resolvedName;
        do {
            resolvedName = this.getAlias(canonicalName);
            if (resolvedName != null) {
                if (name.equals(resolvedName)) {
                    break;
                }
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        return canonicalName;
    }

    /* ====================== attribute ======================= */

    /**
     * 获取属性
     *
     * @param name 属性名称
     *
     * @return 属性
     */
    default Object getAttribute(String name) {
        return this.getAttributes().get(name);
    }

    /**
     * 设置属性
     *
     * @param name      属性名称
     * @param attribute 属性
     */
    default void setAttribute(String name, Object attribute) {
        this.getAttributes().put(name, attribute);
    }

    /**
     * 删除属性
     *
     * @param name 属性名称
     */
    default void removeAttribute(String name) {
        this.getAttributes().remove(name);
    }

    /**
     * 获取属性
     *
     * @param name       属性名称
     * @param returnType 返回类型
     * @param <T>        属性类型
     *
     * @return 属性
     */
    default <T> T getAttribute(String name, Class<T> returnType) {
        return Convert.convert(returnType, this.getAttribute(name));
    }

    /**
     * 获取或使用默认属性
     *
     * @param name         属性名称
     * @param defaultValue 默认值
     * @param <T>          属性类型
     *
     * @return 属性
     */
    @SuppressWarnings("unchecked")
    default <T> T getOrDefaultAttribute(String name, T defaultValue) {
        Object attribute = this.getAttribute(name);
        if (attribute == null) {
            this.setAttribute(name, defaultValue);
            return defaultValue;
        }
        return (T) attribute;
    }

    /**
     * 是否存在属性
     *
     * @param name 属性名称
     *
     * @return 是否存在
     */
    default boolean hasAttribute(String name) {
        return this.getAttribute(name) != null;
    }

    /* ====================== instance ======================= */

    /**
     * 获取实例
     *
     * @param name 实例名称
     *
     * @return 实例
     */
    default Object getInstance(String name) {
        Binding binding = this.getBinding(name);
        if (binding == null || !binding.isCreated()) {
            return null;
        }
        return binding.getInstance();
    }

    /**
     * 设置实例
     *
     * @param name     实例名称
     * @param instance 实例
     *
     * @return Binding
     */
    default Binding setInstance(String name, Object instance) {
        Binding binding = this.getBinding(name);
        if (binding == null) {
            binding = new Binding(instance, ScopeType.SINGLETON, name);
            this.setBinding(name, binding);
        } else {
            binding.setInstance(instance);
        }
        return binding;
    }

    /**
     * 是否存在实例
     *
     * @param name 实例名称
     *
     * @return 是否存在
     */
    default boolean hasInstance(String name) {
        Binding binding = this.getBinding(name);
        if (binding == null) {
            return false;
        }
        return binding.isCreated();
    }

    /**
     * 删除实例
     *
     * @param name 实例名称
     */
    default void removeInstance(String name) {
        this.removeBinding(name);
    }

    /**
     * 获取实例
     *
     * @param type 实例类型
     *
     * @return 实例
     */
    default Object getInstance(Class<?> type) {
        return this.getInstance(type.getName());
    }

    /**
     * 设置实例
     *
     * @param type     实例类型
     * @param instance 实例
     */
    default void setInstance(Class<?> type, Object instance) {
        this.setInstance(type.getName(), instance);
    }

    /**
     * 是否存在实例
     *
     * @param type 实例类型
     *
     * @return 是否存在
     */
    default boolean hasInstance(Class<?> type) {
        return this.hasInstance(type.getName());
    }

    /**
     * 删除实例
     *
     * @param type 实例类型
     */
    default void removeInstance(Class<?> type) {
        this.removeInstance(type.getName());
    }
}
