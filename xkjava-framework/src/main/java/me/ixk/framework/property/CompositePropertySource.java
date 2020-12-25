/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.property;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 组合配置源
 *
 * @author Otstar Lin
 * @date 2020/12/21 下午 10:21
 */
public class CompositePropertySource
    extends PropertySource<Map<String, PropertySource<?>>> {

    private final Map<String, Object> changedMap = new ConcurrentHashMap<>();

    public CompositePropertySource(String name) {
        super(name, new ConcurrentHashMap<>());
    }

    public CompositePropertySource(
        final String name,
        final List<PropertySource<?>> source
    ) {
        super(
            name,
            source
                .stream()
                .collect(
                    Collectors.toConcurrentMap(PropertySource::getName, v -> v)
                )
        );
    }

    public CompositePropertySource(
        final String name,
        final Map<String, PropertySource<?>> source
    ) {
        super(name, source);
    }

    @Override
    public Object getProperty(final String name) {
        Object value = this.changedMap.get(name);
        if (value != null) {
            return value;
        }
        for (final PropertySource<?> propertySource : this.source.values()) {
            value = propertySource.get(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void setProperty(String name, Object value) {
        for (PropertySource<?> propertySource : this.source.values()) {
            if (propertySource.has(name)) {
                propertySource.setProperty(name, value);
                return;
            }
        }
        this.changedMap.put(name, value);
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> names = new HashSet<>(this.changedMap.keySet());
        for (PropertySource<?> propertySource : this.source.values()) {
            names.addAll(propertySource.getPropertyNames());
        }
        return names;
    }

    @Override
    public void removeProperty(String name) {
        this.changedMap.remove(name);
        for (PropertySource<?> propertySource : this.source.values()) {
            if (propertySource.has(name)) {
                propertySource.removeProperty(name);
            }
        }
    }

    public void setPropertySource(final PropertySource<?> propertySource) {
        this.source.put(propertySource.getName(), propertySource);
    }

    public void removePropertySource(final String name) {
        this.source.remove(name);
    }
}
