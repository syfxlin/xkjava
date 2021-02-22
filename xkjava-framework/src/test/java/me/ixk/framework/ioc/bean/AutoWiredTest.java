/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.bean;

import me.ixk.framework.annotation.core.Autowired;

/**
 * @author Otstar Lin
 * @date 2020/12/24 上午 1:02
 */
public class AutoWiredTest {

    @Autowired
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
