/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import com.fasterxml.jackson.databind.node.NullNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.utils.Convert;

public class WebDataBinder implements DataBinder {
    public static final String DEFAULT_VALUE_PREFIX = "&";

    private String prefix = "";

    private boolean first = true;

    private final Request request;

    private final Container container;

    private final Map<String, Object> data = new ConcurrentHashMap<>();

    private final Map<String, List<Converter<?>>> converter = new ConcurrentHashMap<>();

    public WebDataBinder(Container container, Request request) {
        this.container = container;
        this.request = request;
    }

    public <T> T getObject(String name, Class<T> type) {
        return this.getObject(name, type, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T getObject(String name, Class<T> type, DataBind dataBind) {
        if (dataBind != null) {
            name =
                dataBind.name().length() == 0
                    ? dataBind.name()
                    : Request.REQUEST_BODY;
            this.prefix = name;
        }
        String concatName = this.concat(name);
        String typeName = type.getName();
        Object object = this.request.all(concatName);
        if (object == NullNode.getInstance()) {
            object = null;
        }
        if (object == null) {
            object = this.data.get(DEFAULT_VALUE_PREFIX + concatName);
        }
        if (object == null) {
            object = this.data.get(typeName);
        }
        if (object == null) {
            object = this.data.get(DEFAULT_VALUE_PREFIX + typeName);
        }
        if (object == null) {
            String oldPrefix = this.prefix;
            boolean oldFirst = this.first;
            this.prefix = this.concatPrefix(name);
            this.first = false;
            object = container.make(type.getName(), (Class<?>) type, this);
            this.prefix = oldPrefix;
            this.first = oldFirst;
        }
        List<Converter<?>> converters = this.converter.get(concatName);
        if (converters != null) {
            for (Converter converter : converters) {
                object = converter.before(object);
            }
        }
        object = Convert.convert(type, object);
        if (converters != null) {
            for (Converter converter : converters) {
                object = converter.after(object);
            }
        }
        return (T) object;
    }

    protected String concatPrefix(String name) {
        if (this.first) {
            return this.prefix;
        }
        return (this.prefix.length() == 0 ? "" : this.prefix + ".") + name;
    }

    protected String concat(String name) {
        return (
            (this.first || this.prefix.length() == 0 ? "" : this.prefix + ".") +
            name
        );
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, List<Converter<?>>> getConverter() {
        return converter;
    }

    public WebDataBinder add(String name, Object object) {
        this.data.put(name, object);
        return this;
    }

    public WebDataBinder addDefault(String name, Object object) {
        return this.add(DEFAULT_VALUE_PREFIX + name, object);
    }

    public WebDataBinder remove(String name) {
        this.data.remove(name);
        return this;
    }

    public WebDataBinder removeDefault(String name) {
        this.data.remove(DEFAULT_VALUE_PREFIX + name);
        return this;
    }

    public WebDataBinder addConverter(String name, Converter<?> converter) {
        List<Converter<?>> converters =
            this.converter.getOrDefault(name, new ArrayList<>());
        converters.add(converter);
        this.converter.put(name, converters);
        return this;
    }

    public WebDataBinder removeConverter(String name) {
        this.converter.remove(name);
        return this;
    }
}
