package me.ixk.framework.facades;

import java.util.Map;
import me.ixk.framework.view.FilterCallback;

public class View extends AbstractFacade {

    protected static me.ixk.framework.view.View make() {
        return app.make(me.ixk.framework.view.View.class);
    }

    public me.ixk.framework.view.View assign(Map<String, Object> data) {
        return make().assign(data);
    }

    public static me.ixk.framework.view.View with(String key, Object value) {
        return make().with(key, value);
    }

    public static me.ixk.framework.view.View make(String view) {
        return make().make(view);
    }

    public static me.ixk.framework.view.View make(
        String view,
        Map<String, Object> data
    ) {
        return make().make(view, data);
    }

    public static me.ixk.framework.view.View make(
        String view,
        Map<String, Object> data,
        FilterCallback filterCallback
    ) {
        return make().make(view, data, filterCallback);
    }

    public static me.ixk.framework.view.View filter(FilterCallback callback) {
        return make().filter(callback);
    }

    public static String render() {
        return make().render();
    }
}
