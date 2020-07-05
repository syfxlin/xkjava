/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Value;

@Data
@NoArgsConstructor
@Bean
public class User3 {
    @Value("$('test.name')")
    String name;

    @Value("$('age:21')")
    int age;
}
