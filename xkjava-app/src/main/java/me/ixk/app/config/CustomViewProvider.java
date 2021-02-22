/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import me.ixk.framework.annotation.core.Provider;
import me.ixk.framework.view.TemplateProcessor;
import me.ixk.framework.view.Thymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Provider
public class CustomViewProvider {

    // @Bean(name = "templateProcessor")
    public TemplateProcessor templateProcessor() {
        final ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/template/");
        resolver.setSuffix(".html");
        resolver.setCacheTTLMs(3600000L);
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return new Thymeleaf(templateEngine);
    }
}
