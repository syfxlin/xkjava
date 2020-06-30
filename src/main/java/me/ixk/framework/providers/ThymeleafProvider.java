package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.utils.Thymeleaf;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class ThymeleafProvider extends AbstractProvider {

    public ThymeleafProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(
                Thymeleaf.class,
                (container, with) ->
                    new Thymeleaf(
                        container
                            .make(DispatcherServlet.class)
                            .getServletContext()
                    ),
                "thymeleaf"
            );
    }
}
