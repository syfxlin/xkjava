/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ixk.framework.util.LinkedCaseInsensitiveMultiValueMap;
import me.ixk.framework.util.MultiValueMap;
import org.jetbrains.annotations.NotNull;

/**
 * HTTP 头字段（多）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:15
 */
public class HttpHeaders
    implements MultiValueMap<String, String>, Serializable {

    private static final long serialVersionUID = 1L;

    private final MultiValueMap<String, String> headers = new LinkedCaseInsensitiveMultiValueMap<>();

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return this.headers.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return this.headers.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return this.headers.remove(key);
    }

    @Override
    public void putAll(
        @NotNull Map<? extends String, ? extends List<String>> m
    ) {
        this.headers.putAll(m);
    }

    @Override
    public void clear() {
        this.headers.clear();
    }

    @Override
    public @NotNull Set<String> keySet() {
        return this.headers.keySet();
    }

    @Override
    public @NotNull Collection<List<String>> values() {
        return this.headers.values();
    }

    @Override
    public @NotNull Set<Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }

    public List<String> getOrEmpty(String headerName) {
        return this.headers.getOrDefault(headerName, Collections.emptyList());
    }

    public String getOne(HttpHeader key) {
        return this.getOne(key.asString());
    }

    public void add(HttpHeader key, String value) {
        this.add(key.asString(), value);
    }

    public void addAll(HttpHeader key, List<String> values) {
        this.addAll(key.asString(), values);
    }

    public void addAll(HttpHeader key, String[] values) {
        this.addAll(key.asString(), values);
    }

    public void set(HttpHeader key, List<String> values) {
        this.set(key.asString(), values);
    }

    public void set(HttpHeader key, String[] values) {
        this.set(key.asString(), values);
    }

    public void set(HttpHeader key, String value) {
        this.set(key.asString(), value);
    }
}
