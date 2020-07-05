/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import lombok.Data;
import me.ixk.framework.annotations.ConfigurationProperties;

@ConfigurationProperties(value = "test")
@Data
public class TestConfigurationProperties {
    private String name;
    private Integer age;
}
