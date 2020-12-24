/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.bean;

/**
 * @author Otstar Lin
 * @date 2020/12/24 上午 9:04
 */
public class TypeUser {

    private final User user;

    public TypeUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
