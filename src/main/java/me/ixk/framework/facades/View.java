package me.ixk.framework.facades;

import me.ixk.framework.view.FilterCallback;
import me.ixk.framework.view.ViewResult;

import java.util.Map;

public class View extends AbstractFacade {

    protected static ViewResult make() {
        return app.make(ViewResult.class);
    }

    public ViewResult assign(Map<String, Object> data) {
        return make().assign(data);
    }

    public static ViewResult with(String key, Object value) {
        return make().with(key, value);
    }

    public static ViewResult make(String view) {
        return make().make(view);
    }

    public static ViewResult make(
        String view,
        Map<String, Object> data
    ) {
        return make().make(view, data);
    }

    public static ViewResult make(
        String view,
        Map<String, Object> data,
        FilterCallback filterCallback
    ) {
        return make().make(view, data, filterCallback);
    }

    public static ViewResult filter(FilterCallback callback) {
        return make().filter(callback);
    }

    public static String render() {
        return make().render();
    }
}
