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

/**
 * Thymeleaf
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:16
 */
public class Thymeleaf implements TemplateProcessor {
    protected final TemplateEngine templateEngine;

    public Thymeleaf(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    @Override
    public String process(String templateName, Map<String, Object> values) {
        XkJava app = XkJava.of();
        WebContext webContext = new WebContext(
            app.make(Request.class),
            app.make(Response.class),
            app.make(DispatcherServlet.class).getServletContext()
        );
        webContext.setVariables(values);
        return this.templateEngine.process(templateName, webContext);
    }
}
