/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import lombok.Data;

@Data
public class User2 {
    private String name;
    private int age;

    // @Autowired // 加上该注解后容器会优先选用该构造器实例化
    public User2() {}

    public User2(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
