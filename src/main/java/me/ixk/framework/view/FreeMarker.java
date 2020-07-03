package me.ixk.framework.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import me.ixk.framework.exceptions.TemplateException;
import me.ixk.framework.utils.ByteArrayUtf8Writer;

import java.io.IOException;
import java.util.Map;

public class FreeMarker implements TemplateProcessor {
    protected final Configuration configuration;

    public FreeMarker() {
        Configuration configuration = new Configuration(
            Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS
        );
        configuration.setClassForTemplateLoading(
            FreeMarker.class,
            "/templates/"
        );
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(
            TemplateExceptionHandler.DEBUG_HANDLER
        );
        this.configuration = configuration;
    }

    public String process(String templateName, Map<String, Object> values) {
        if (!templateName.endsWith(".ftl")) {
            templateName += ".ftl";
        }
        Template template;
        try {
            template = this.configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new TemplateException(e);
        }
        ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer(4096);
        try {
            template.process(values, writer);
        } catch (freemarker.template.TemplateException | IOException e) {
            throw new TemplateException(e);
        }
        return writer.toString();
    }
}
