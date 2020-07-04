/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.ixk.framework.annotations.SkipPropertyAutowired;

@Data
@AllArgsConstructor
@SkipPropertyAutowired
public class User {
    protected String name;
    protected int age;

    protected User2 user;
}
