/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.util.Map;
import me.ixk.framework.exceptions.TemplateException;
import me.ixk.framework.utils.ByteArrayUtf8Writer;

/**
 * FreeMarker
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:15
 */
public class FreeMarker implements TemplateProcessor {
    protected final Configuration configuration;

    public FreeMarker(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
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
