/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.entity;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User2 {
    @NotNull
    private String name;

    @NotNull
    private Integer age;

    // @Autowired // 加上该注解后容器会优先选用该构造器实例化
    public User2() {}

    public User2(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
