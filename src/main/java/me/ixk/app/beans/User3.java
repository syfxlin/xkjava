/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ixk.framework.annotations.Bean;

@Data
@NoArgsConstructor
@Bean
public class User3 {
    String name = "name";
    Integer age = 10;
}
