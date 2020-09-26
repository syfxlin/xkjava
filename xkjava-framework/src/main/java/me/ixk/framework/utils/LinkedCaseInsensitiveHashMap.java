/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LinkedCaseInsensitiveHashMap<V> extends LinkedHashMap<String, V> {
    private final HashMap<String, String> caseInsensitiveKeys = new HashMap<>();

    public LinkedCaseInsensitiveHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinkedCaseInsensitiveHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedCaseInsensitiveHashMap() {
        super();
    }

    public LinkedCaseInsensitiveHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LinkedCaseInsensitiveHashMap(Map<? extends String, ? extends V> m) {
        super(m.size());
        this.putAll(m);
    }

    protected String convertKey(String key) {
        return key.toLowerCase();
    }

    protected String getKey(String key) {
        return this.caseInsensitiveKeys.get(convertKey(key));
    }

    protected void setKey(String key) {
        this.caseInsensitiveKeys.put(convertKey(key), key);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.getKey((String) key);
            if (caseInsensitiveKey != null) {
                return super.get(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.getKey((String) key);
            if (caseInsensitiveKey != null) {
                return super.getOrDefault(caseInsensitiveKey, defaultValue);
            }
        }
        return defaultValue;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.getKey((String) key);
            if (caseInsensitiveKey != null) {
                return super.containsKey(caseInsensitiveKey);
            }
        }
        return false;
    }

    @Override
    public V put(String key, V value) {
        this.setKey(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Map.Entry<? extends String, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.getKey((String) key);
            if (caseInsensitiveKey != null) {
                return super.remove(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public V putIfAbsent(String key, V value) {
        String oldKey =
            this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            return super.get(oldKey);
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.getKey((String) key);
            if (caseInsensitiveKey != null) {
                return super.remove(caseInsensitiveKey, value);
            }
        }
        return false;
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.replace(caseInsensitiveKey, oldValue, newValue);
        }
        return false;
    }

    @Override
    public V replace(String key, V value) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.replace(caseInsensitiveKey, value);
        }
        return null;
    }

    @Override
    public V computeIfAbsent(
        String key,
        Function<? super String, ? extends V> mappingFunction
    ) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.computeIfAbsent(caseInsensitiveKey, mappingFunction);
        }
        return null;
    }

    @Override
    public V computeIfPresent(
        String key,
        BiFunction<? super String, ? super V, ? extends V> remappingFunction
    ) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.computeIfPresent(
                caseInsensitiveKey,
                remappingFunction
            );
        }
        return null;
    }

    @Override
    public V compute(
        String key,
        BiFunction<? super String, ? super V, ? extends V> remappingFunction
    ) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.compute(caseInsensitiveKey, remappingFunction);
        }
        return null;
    }

    @Override
    public V merge(
        String key,
        V value,
        BiFunction<? super V, ? super V, ? extends V> remappingFunction
    ) {
        String caseInsensitiveKey = this.getKey(key);
        if (caseInsensitiveKey != null) {
            return super.merge(caseInsensitiveKey, value, remappingFunction);
        }
        return null;
    }

    @Override
    public void clear() {
        super.clear();
        this.caseInsensitiveKeys.clear();
    }
}
