/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.filter.EncodingFilter;

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
}
