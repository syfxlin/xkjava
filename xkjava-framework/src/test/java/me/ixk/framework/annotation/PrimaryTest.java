/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.Primary;
import me.ixk.framework.entity.User2;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/12/24 下午 10:44
 */
@XkJavaTest
class PrimaryTest {

    @Autowired
    XkJava app;

    @Test
    void make() {
        final User2 user2 = this.app.make("user2", User2.class);
        assertEquals("otstar", user2.getName());
    }

    @Bean
    public User2 user1() {
        return new User2("syfxlin", 20);
    }

    @Bean
    @Primary
    public User2 user2() {
        return new User2("otstar", 20);
    }
}
