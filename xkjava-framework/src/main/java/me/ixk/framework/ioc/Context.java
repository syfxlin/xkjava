/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.utils.Convert;

public interface Context {
    String ATTRIBUTE_PREFIX = "$";

    // 基础字段
    Map<String, String> getAliases();

    Map<String, Binding> getBindings();

    String getName();

    // 匹配的 ScopeType
    boolean matchesScope(ScopeType scopeType);

    // 该 Context 是否启动，一般的 Context 只要 new 后就会启动
    // 但是如果是 ThreadLocal 则需要另行启动
    default boolean isCreated() {
        return true;
    }

    /* ====================== binding ======================= */

    default Binding getBinding(String name) {
        name = this.getCanonicalName(name);
        return this.getBindings().get(name);
    }

    default Binding setBinding(String name, Binding binding) {
        name = this.getCanonicalName(name);
        this.getBindings().put(name, binding);
        return binding;
    }

    default boolean hasBinding(String name) {
        name = this.getCanonicalName(name);
        return this.getBindings().containsKey(name);
    }

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

    default Binding getBinding(Class<?> requiredType) {
        return this.getBinding(requiredType.getName());
    }

    default Binding setBinding(Class<?> requiredType, Binding binding) {
        return this.setBinding(requiredType.getName(), binding);
    }

    default boolean hasBinding(Class<?> requiredType) {
        return this.hasBinding(requiredType.getName());
    }

    default void removeBinding(Class<?> requiredType) {
        this.removeBinding(requiredType.getName());
    }

    /* ====================== alias ======================= */

    default void registerAlias(String alias, String name) {
        this.getAliases().put(alias, name);
    }

    default void removeAlias(String alias) {
        this.getAliases().remove(alias);
    }

    default boolean hasAlias(String alias) {
        return this.getAliases().containsKey(alias);
    }

    default String getAlias(String alias) {
        return this.getAliases().get(alias);
    }

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

    // attribute 是一种特殊的 binding，也可以认为是已经创建好的单例，attribute 会自动在名称前添加 $ 前缀用以区分
    default Object getAttribute(String name) {
        Binding binding = this.getBinding(ATTRIBUTE_PREFIX + name);
        return binding == null ? null : binding.getInstance();
    }

    default <T> T getAttribute(String name, Class<T> returnType) {
        return Convert.convert(returnType, this.getAttribute(name));
    }

    @SuppressWarnings("unchecked")
    default <T> T getOrDefaultAttribute(String name, T _default) {
        Binding result = this.getBinding(ATTRIBUTE_PREFIX + name);
        if (result == null) {
            this.setAttribute(name, _default);
            return _default;
        }
        return (T) result.getInstance();
    }

    default Binding setAttribute(String name, Object attribute) {
        return this.setBinding(
                ATTRIBUTE_PREFIX + name,
                new Binding(attribute, ScopeType.SINGLETON)
            );
    }

    default void removeAttribute(String name) {
        this.removeBinding(ATTRIBUTE_PREFIX + name);
    }

    default boolean hasAttribute(String name) {
        return this.hasBinding(ATTRIBUTE_PREFIX + name);
    }

    /* ====================== instance ======================= */

    default Object getInstance(String name) {
        Binding binding = this.getBinding(name);
        if (binding == null || !binding.isCreated()) {
            return null;
        }
        return binding.getInstance();
    }

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

    default boolean hasInstance(String name) {
        Binding binding = this.getBinding(name);
        if (binding == null) {
            return false;
        }
        return binding.isCreated();
    }

    default void removeInstance(String name) {
        this.removeBinding(name);
    }

    default Object getInstance(Class<?> type) {
        return this.getInstance(type.getName());
    }

    default void setInstance(Class<?> type, Object instance) {
        this.setInstance(type.getName(), instance);
    }

    default boolean hasInstance(Class<?> type) {
        return this.hasInstance(type.getName());
    }

    default void removeInstance(Class<?> type) {
        this.removeInstance(type.getName());
    }
}
