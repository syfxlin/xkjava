/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.beans;

import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;

/**
 * @author Otstar Lin
 * @date 2020/10/26 下午 10:05
 */
@Bean
@Scope(type = ScopeType.SESSION)
public class SessionTest {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
