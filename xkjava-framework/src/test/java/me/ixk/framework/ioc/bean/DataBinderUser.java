/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.bean;

/**
 * @author Otstar Lin
 * @date 2020/12/24 上午 9:13
 */
public class DataBinderUser {

    private final String name;

    public DataBinderUser(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
