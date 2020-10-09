/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.view.FreeMarker;
import me.ixk.framework.view.TemplateProcessor;

@Configuration
public class ViewProvider {

    @Bean(name = "templateProcessor")
    @ConditionalOnMissingBean(
        name = "templateProcessor",
        value = TemplateProcessor.class
    )
    public TemplateProcessor templateProcessor() {
        return new FreeMarker();
    }
}
