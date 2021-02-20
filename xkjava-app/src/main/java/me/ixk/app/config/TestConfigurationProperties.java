/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.config;

import lombok.Data;
import me.ixk.framework.annotation.ConfigurationProperties;
import me.ixk.framework.annotation.Value;

@ConfigurationProperties(value = "test")
@Data
public class TestConfigurationProperties {

    private String name;

    @Value("#{#e['test.age1']}")
    private Integer age;

    private String nickName;
}
