/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;

/**
 * 响应模型
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 10:04
 */
@Component(name = "responseModel")
@Scope(type = ScopeType.REQUEST)
public class Model extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 9097159139320901236L;

    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Model addAttribute(final String name, final Object value) {
        this.put(name, value);
        return this;
    }

    public Model removeAttribute(final String name) {
        this.remove(name);
        return this;
    }

    public Model addAllAttribute(final Map<String, Object> map) {
        for (final Entry<String, Object> entry : map.entrySet()) {
            this.addAttribute(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public boolean containsAttribute(final String attributeName) {
        return this.containsKey(attributeName);
    }

    public Object getAttribute(final String attributeName) {
        return this.get(attributeName);
    }
}