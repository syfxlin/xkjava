/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.view.FreeMarker;
import me.ixk.framework.view.TemplateProcessor;

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
                (container, with) -> new FreeMarker(),
                "templateProcessor"
            );
    }
}
