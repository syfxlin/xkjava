/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

import java.util.Map;

/**
 * 视图处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:15
 */
public interface TemplateProcessor {
    /**
     * 处理
     *
     * @param templateName 模板名称
     * @param values       注入的值
     *
     * @return 解析后的结果
     */
    String process(String templateName, Map<String, Object> values);
}
