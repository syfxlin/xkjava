package me.ixk.framework.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.Renderable;
import me.ixk.framework.ioc.Application;

public class ViewResult implements Renderable {
    protected String view = "";

    protected final Map<String, Object> data = new ConcurrentHashMap<>();

    protected FilterCallback filterCallback = null;

    public ViewResult assign(Map<String, Object> data) {
        this.data.putAll(data);
        if (!this.data.containsKey("errors")) {
            this.data.put("errors", new ConcurrentHashMap<>());
        }
        return this;
    }

    public ViewResult with(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ViewResult make(String view) {
        this.view = view;
        return this;
    }

    public ViewResult make(String view, Map<String, Object> data) {
        this.view = view;
        this.assign(data);
        return this;
    }

    public ViewResult make(
        String view,
        Map<String, Object> data,
        FilterCallback filterCallback
    ) {
        this.view = view;
        this.assign(data);
        this.filterCallback = filterCallback;
        return this;
    }

    public ViewResult filter(FilterCallback callback) {
        this.filterCallback = callback;
        return this;
    }

    @Override
    public String render() {
        String html = Application
            .get()
            .make(TemplateProcessor.class)
            .process(this.view, this.data);
        if (this.filterCallback != null) {
            html = this.filterCallback.filter(html);
        }
        return html;
    }
}
