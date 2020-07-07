/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.WebContext;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.FilterCallback;
import me.ixk.framework.view.TemplateProcessor;
import org.eclipse.jetty.http.MimeTypes;

public class ViewResult extends HttpResult {
    protected String view;

    protected Map<String, Object> data = new ConcurrentHashMap<>();

    protected FilterCallback filterCallback = null;

    public ViewResult(String view) {
        this.view = view;
    }

    public ViewResult(String view, Map<String, Object> data) {
        this(view, data, null);
    }

    public ViewResult(
        String view,
        Map<String, Object> data,
        FilterCallback callback
    ) {
        this.view = view;
        this.assign(data);
        this.filterCallback = callback;
    }

    public ViewResult with(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ViewResult assign(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public ViewResult add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ViewResult filter(FilterCallback callback) {
        this.filterCallback = callback;
        return this;
    }

    public void view(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public FilterCallback getFilter() {
        return filterCallback;
    }

    private void injectModel() {
        WebContext context = Application.get().make(WebContext.class);
        // 注入
        this.data.put("$context", context);
        this.data.put("$application", context.getApplication());
        this.data.put("$config", context.getConfig());
        this.data.put("$environment", context.getEnvironment());
        this.data.put("$servlet", context.getServlet());
        this.data.put("$request", context.getRequest());
        this.data.put("$response", context.getResponse());
        this.data.put("$cookie", context.getCookieManager());
        this.data.put("$session", context.getSessionManager());
    }

    @Override
    public String render() {
        this.injectModel();
        String html = Application
            .get()
            .make(TemplateProcessor.class)
            .process(this.view, this.data);
        if (this.filterCallback != null) {
            html = this.filterCallback.filter(html);
        }
        return html;
    }

    @Override
    public String contentType() {
        return MimeTypes.Type.TEXT_HTML.asString();
    }
}
