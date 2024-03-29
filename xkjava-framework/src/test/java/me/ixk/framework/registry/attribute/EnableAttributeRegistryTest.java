/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.attribute;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import me.ixk.framework.annotation.condition.ConditionalOnEnable;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.Enable;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:33
 */
@XkJavaTest
@Enable(name = "testEnable", classes = EnableAttributeRegistryTest.class)
class EnableAttributeRegistryTest {

    @Autowired
    XkJava app;

    @Test
    void registry() {
        assertTrue(
            app.enableFunctions().contains("testEnable") &&
            app
                .enableFunctions()
                .contains(EnableAttributeRegistryTest.class.getName())
        );
        assertTrue(app.has("enable"));
        assertFalse(app.has("disable"));
    }

    @ConditionalOnEnable(name = "testEnable")
    @Bean
    public String enable() {
        return "enable";
    }

    @ConditionalOnEnable("testDisable")
    @Bean
    public void disable() {
        fail("Must be disable");
    }
}
