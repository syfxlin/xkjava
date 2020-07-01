package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.TemplateProcessor;
import me.ixk.framework.view.Thymeleaf;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class TemplateProcessorProvider extends AbstractProvider {

    public TemplateProcessorProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(
                TemplateProcessor.class,
                (container, with) -> new Thymeleaf(),
                "templateProcessor"
            );
    }
}
