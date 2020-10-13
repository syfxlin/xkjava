/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.ObjectWrapperDataBinder;

public class WebDataBinder extends ObjectWrapperDataBinder {
    public static final String DEFAULT_VALUE_PREFIX = "&";

    private final Map<String, Object> data = new ConcurrentHashMap<>();

    public WebDataBinder(Container container, Request request) {
        this(container, request, new ArrayList<>());
    }

    public WebDataBinder(
        Container container,
        Request request,
        List<Function<String, Object>> getters
    ) {
        super(container, getters);
        this.getters.add(request::all);
        this.getters.add(data::get);
    }

    public WebDataBinder(
        Container container,
        String prefix,
        List<Function<String, Object>> getters
    ) {
        super(container, prefix, getters);
    }

    public Map<String, Object> getData() {
        return data;
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

    @Override
    protected String getDefaultDataBindName() {
        return Request.REQUEST_BODY;
    }
}
