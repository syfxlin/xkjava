/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.provider;

import me.ixk.framework.annotation.condition.ConditionalOnMissingBean;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.Provider;
import me.ixk.framework.filter.CorsFilter;
import me.ixk.framework.filter.EncodingFilter;
import me.ixk.framework.web.CorsProcessor;

/**
 * 过滤器提供器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 11:01
 */
@Provider
public class FilterProvider {

    @Bean(name = "encodingFilter")
    @ConditionalOnMissingBean(
        name = "encodingFilter",
        value = EncodingFilter.class
    )
    public EncodingFilter encodingFilter() {
        return new EncodingFilter("UTF-8");
    }

    @Bean(name = "corsFilter")
    @ConditionalOnMissingBean(name = "corsFilter", value = CorsFilter.class)
    public CorsFilter corsFilter(final CorsProcessor processor) {
        return new CorsFilter(processor);
    }
}
