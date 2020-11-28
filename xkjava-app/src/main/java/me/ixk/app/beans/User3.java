/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Value;

@Data
@NoArgsConstructor
@Bean
// @Conditional({ FalseConditional.class })
// @ConditionalOnBean(name = { "app" })
// @ConditionalOnMissingBean(name = { "C" })
// @ConditionalOnProperty(
//     prefix = "test.",
//     name = { "name" },
//     havingValue = "Otstar Lin"
// )
// @ConditionalOnExpression("2 > 1")
// @ConditionalOnResource(resources = { "banner.txt" })
// @ConditionalOnJava(version = JavaVersion.FIFTEEN)
@ConfigurationProperties
public class User3 {

    @Value("#{#e['test.name']}")
    String name;

    @Value("#{#e['age'] ?: 21}")
    int age;
}
