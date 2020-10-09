/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.view.FreeMarker;
import me.ixk.framework.view.TemplateProcessor;

@Provider
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
