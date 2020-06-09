package me.ixk.framework.utils;

import java.util.Map;
import javax.servlet.ServletContext;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.servlet.DispatcherServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class Thymeleaf {
    protected TemplateEngine templateEngine;

    public Thymeleaf(ServletContext servletContext) {
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

    public String process(String template, Map<String, Object> values) {
        Application app = Application.get();
        WebContext webContext = new WebContext(
            app.make(Request.class).getOriginRequest(),
            app.make(Response.class).getOriginResponse(),
            app.make(DispatcherServlet.class).getServletContext()
        );
        webContext.setVariables(values);
        return this.templateEngine.process(template, webContext);
    }
}
