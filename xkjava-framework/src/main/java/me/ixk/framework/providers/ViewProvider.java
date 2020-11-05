/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
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
    public TemplateProcessor templateProcessor(Configuration configuration) {
        return new FreeMarker(configuration);
    }

    @Bean(name = "freemarkerConfiguration")
    @ConditionalOnMissingBean(
        name = "freemarkerConfiguration",
        value = Configuration.class
    )
    public Configuration freemarkerConfiguration() {
        Configuration configuration = new Configuration(
            Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS
        );
        configuration.setClassForTemplateLoading(
            FreeMarker.class,
            "/template/"
        );
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(
            TemplateExceptionHandler.DEBUG_HANDLER
        );
        configuration.setAPIBuiltinEnabled(true);
        DefaultObjectWrapper defaultObjectWrapper = (DefaultObjectWrapper) configuration.getObjectWrapper();
        defaultObjectWrapper.setUseAdaptersForContainers(true);
        return configuration;
    }
}
