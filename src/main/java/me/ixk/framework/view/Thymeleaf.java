/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

import java.util.Map;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.servlet.DispatcherServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class Thymeleaf implements TemplateProcessor {
    protected final TemplateEngine templateEngine;

    public Thymeleaf() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheTTLMs(3600000L);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public String process(String templateName, Map<String, Object> values) {
        Application app = Application.get();
        WebContext webContext = new WebContext(
            app.make(Request.class).getOriginRequest(),
            app.make(Response.class).getOriginResponse(),
            app.make(DispatcherServlet.class).getServletContext()
        );
        webContext.setVariables(values);
        return this.templateEngine.process(templateName, webContext);
    }
}