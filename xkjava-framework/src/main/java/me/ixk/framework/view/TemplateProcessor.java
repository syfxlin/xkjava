/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

import java.util.Map;

public interface TemplateProcessor {
  String process(String templateName, Map<String, Object> values);
}
