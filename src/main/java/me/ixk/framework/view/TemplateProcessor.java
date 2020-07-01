package me.ixk.framework.view;

import java.util.Map;

public interface TemplateProcessor {
    String process(String templateName, Map<String, Object> values);
}
