/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.view.FreeMarker;
import me.ixk.framework.view.TemplateProcessor;

/**
 * 视图提供者
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:52
 */
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
