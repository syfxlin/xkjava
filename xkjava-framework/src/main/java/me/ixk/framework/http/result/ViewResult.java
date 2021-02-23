/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.view.FilterCallback;
import me.ixk.framework.view.TemplateProcessor;
import me.ixk.framework.web.WebContext;

/**
 * 视图响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:12
 */
public class ViewResult extends AbstractHttpResult {

    protected String view;

    protected Map<String, Object> data = new ConcurrentHashMap<>();

    protected FilterCallback filterCallback = null;

    public ViewResult(final String view) {
        this.view = view;
    }

    public ViewResult(final String view, final Map<String, Object> data) {
        this(view, data, null);
    }

    public ViewResult(
        final String view,
        final Map<String, Object> data,
        final FilterCallback callback
    ) {
        this.view = view;
        this.assign(data);
        this.filterCallback = callback;
    }

    public ViewResult data(final Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ViewResult assign(final Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public ViewResult add(final String key, final Object value) {
        this.data.put(key, value);
        return this;
    }

    public ViewResult filter(final FilterCallback callback) {
        this.filterCallback = callback;
        return this;
    }

    public void view(final String view) {
        this.view = view;
    }

    public String view() {
        return view;
    }

    public Map<String, Object> data() {
        return data;
    }

    public FilterCallback filter() {
        return filterCallback;
    }

    private void injectModel() {
        final WebContext context = XkJava.of().make(WebContext.class);
        // 注入
        this.data.put("$context", context);
        this.data.put("$application", context.app());
        this.data.put("$environment", context.env());
        this.data.put("$servlet", context.servlet());
        this.data.put("$request", context.request());
        this.data.put("$response", context.response());
        this.data.put("$cookie", context.cookie());
        this.data.put("$session", context.session());
    }

    @Override
    public String render() {
        this.injectModel();
        String html = XkJava
            .of()
            .make(TemplateProcessor.class)
            .process(this.view, this.data);
        if (this.filterCallback != null) {
            html = this.filterCallback.filter(html);
        }
        return html;
    }

    @Override
    public String contentType() {
        return MimeType.TEXT_HTML.asString();
    }
}
