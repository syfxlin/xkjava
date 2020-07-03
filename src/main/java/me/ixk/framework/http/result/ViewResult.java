package me.ixk.framework.http.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.FilterCallback;
import me.ixk.framework.view.TemplateProcessor;
import org.eclipse.jetty.http.MimeTypes;

public class ViewResult extends HttpResult {
    protected String view = "";

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

    public static ViewResult make(String view) {
        return new ViewResult(view);
    }

    public static ViewResult make(String view, Map<String, Object> data) {
        return new ViewResult(view, data);
    }

    public static ViewResult make(
        String view,
        Map<String, Object> data,
        FilterCallback filterCallback
    ) {
        return new ViewResult(view, data, filterCallback);
    }

    public ViewResult with(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ViewResult assign(Map<String, Object> data) {
        this.data.putAll(data);
        if (!this.data.containsKey("errors")) {
            this.data.put("errors", new ConcurrentHashMap<>());
        }
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

    @Override
    public String contentType() {
        return MimeTypes.Type.TEXT_HTML.asString();
    }
}
