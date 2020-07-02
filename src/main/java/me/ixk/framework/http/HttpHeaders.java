package me.ixk.framework.http;

import java.io.Serializable;
import java.util.*;
import me.ixk.framework.utils.LinkedCaseInsensitiveMultiValueMap;
import me.ixk.framework.utils.MultiValueMap;
import org.eclipse.jetty.http.HttpHeader;

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
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        this.headers.putAll(m);
    }

    @Override
    public void clear() {
        this.headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return this.headers.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
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
