/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import me.ixk.framework.view.TemplateProcessor;
import me.ixk.framework.view.Thymeleaf;

// @Configuration
public class CustomViewProvider {

    // @Bean(name = "templateProcessor")
    public TemplateProcessor templateProcessor() {
        return new Thymeleaf();
    }
}
