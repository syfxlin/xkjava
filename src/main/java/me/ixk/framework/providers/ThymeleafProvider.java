package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.utils.Thymeleaf;

public class ThymeleafProvider extends AbstractProvider {

    public ThymeleafProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(
                Thymeleaf.class,
                (container, args) ->
                    new Thymeleaf(
                        container
                            .make(DispatcherServlet.class)
                            .getServletContext()
                    ),
                "thymeleaf"
            );
    }
}
