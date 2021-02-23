/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.ixk.framework.annotation.web.WebBind;
import me.ixk.framework.annotation.web.WebBind.Type;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.binder.DataBinder;
import me.ixk.framework.ioc.binder.ObjectWrapperDataBinder;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.MergedAnnotation;
import org.jetbrains.annotations.NotNull;

/**
 * Web 数据绑定器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:23
 */
public class WebDataBinder extends ObjectWrapperDataBinder {

    private final Map<String, Object> data;
    private final Map<Type, Function<String, Object>> typeFunctions;

    public WebDataBinder(final Request request) {
        this(null, new ArrayList<>(), request);
    }

    public WebDataBinder(
        final String prefix,
        final List<Function<String, Object>> getters,
        final Request request
    ) {
        super(prefix, getters);
        this.data = new ConcurrentHashMap<>();
        this.getters.add(data::get);
        this.typeFunctions = new ConcurrentHashMap<>();
        // Add type functions
        this.typeFunctions.put(Type.ALL, request::all);
        this.typeFunctions.put(Type.QUERY, request::query);
        this.typeFunctions.put(Type.BODY, request::body);
        this.typeFunctions.put(Type.PATH, request::path);
        this.typeFunctions.put(Type.PART, request::part);
        this.typeFunctions.put(Type.HEADER, request::header);
        this.typeFunctions.put(Type.COOKIE, request::cookie);
        this.typeFunctions.put(Type.SESSION, request::session);
        this.typeFunctions.put(Type.ATTRIBUTE, request::attribute);
    }

    private WebDataBinder(
        final String prefix,
        final Map<String, Object> data,
        final List<Function<String, Object>> getters,
        final Map<Type, Function<String, Object>> typeFunctions
    ) {
        super(prefix, getters);
        this.typeFunctions = typeFunctions;
        this.data = data;
    }

    @Override
    public <T> T getObject(
        final String name,
        final TypeWrapper<T> type,
        final MergedAnnotation annotation,
        final Container container
    ) {
        final WebBind bind = annotation.getAnnotation(WebBind.class);
        final Function<String, Object> function =
            this.typeFunctions.get(bind == null ? Type.ALL : bind.type());
        final List<Converter> converters = bind == null
            ? Collections.emptyList()
            : Arrays
                .stream(bind.converter())
                .map(container::make)
                .collect(Collectors.toList());
        final int size = this.converters.size();
        this.converters.addAll(converters);
        this.getters.add(0, function);
        final T result = super.getObject(name, type, annotation, container);
        this.getters.remove(0);
        for (int i = 0; i < converters.size(); i++) {
            this.converters.remove(size + i);
        }
        return result;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    protected DataBinder copy(@NotNull final String prefix) {
        return new WebDataBinder(
            prefix,
            this.data,
            this.getters,
            this.typeFunctions
        );
    }

    public WebDataBinder add(
        @NotNull final String name,
        @NotNull final Object object
    ) {
        this.data.put(name, object);
        return this;
    }

    public WebDataBinder addDefault(
        @NotNull final String name,
        @NotNull final Object object
    ) {
        return this.add(DEFAULT_VALUE_PREFIX + name, object);
    }

    public WebDataBinder remove(@NotNull final String name) {
        this.data.remove(name);
        return this;
    }

    public WebDataBinder removeDefault(@NotNull final String name) {
        this.data.remove(DEFAULT_VALUE_PREFIX + name);
        return this;
    }
}
