/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

import java.util.Map;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.servlet.DispatcherServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Thymeleaf
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:16
 */
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

    @Override
    public String process(String templateName, Map<String, Object> values) {
        XkJava app = XkJava.of();
        WebContext webContext = new WebContext(
            app.make(Request.class).getOriginRequest(),
            app.make(Response.class).getOriginResponse(),
            app.make(DispatcherServlet.class).getServletContext()
        );
        webContext.setVariables(values);
        return this.templateEngine.process(templateName, webContext);
    }
}
