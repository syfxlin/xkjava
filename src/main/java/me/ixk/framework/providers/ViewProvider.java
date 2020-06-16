package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.View;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 5)
public class ViewProvider extends AbstractProvider {

    public ViewProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(View.class, View.class, ScopeType.PROTOTYPE, "view");
    }
}
