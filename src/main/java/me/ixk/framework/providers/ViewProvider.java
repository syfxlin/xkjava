package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.View;

public class ViewProvider extends AbstractProvider {

    public ViewProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(View.class, View.class, false, "view");
    }
}
