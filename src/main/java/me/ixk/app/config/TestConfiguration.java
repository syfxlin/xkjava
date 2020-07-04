/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public TestConfiguration testConfiguration() {
        return this;
    }
}
