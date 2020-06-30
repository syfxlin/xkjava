package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class AspectProvider extends AbstractProvider {

    public AspectProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        // this.app.singleton(
        //         AspectManager.class,
        //         (container, with) -> new AspectManager(app),
        //         "aspect"
        //     );
    }

    @Override
    public void boot() {
        // this.app.make(AspectManager.class);
    }
}
