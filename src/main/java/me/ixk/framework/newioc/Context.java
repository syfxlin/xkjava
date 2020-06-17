package me.ixk.framework.newioc;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;

public interface Context {
    Map<String, String> getAliases();
    Map<String, Binding> getBindings();

    default Binding getBinding(String name) {
        name = this.getCanonicalName(name);
        return this.getBindings().get(name);
    }

    default void setBinding(String name, Binding binding) {
        name = this.getCanonicalName(name);
        this.getBindings().put(name, binding);
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

    default void setBinding(Class<?> requiredType, Binding binding) {
        this.setBinding(requiredType.getName(), binding);
    }

    default boolean hasBinding(Class<?> requiredType) {
        return this.hasBinding(requiredType.getName());
    }

    default void removeBinding(Class<?> requiredType) {
        this.removeBinding(requiredType.getName());
    }

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
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        return canonicalName;
    }

    default Object getAttribute(String name) {
        return this.getBinding(name).getInstance();
    }

    default void setAttribute(String name, Object attribute) {
        this.setBinding(name, new Binding(attribute, ScopeType.SINGLETON));
    }

    default void removeAttribute(String name) {
        this.removeBinding(name);
    }

    default boolean hasAttribute(String name) {
        return this.hasBinding(name);
    }
}
