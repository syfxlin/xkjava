/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.attribute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnEnable;
import me.ixk.framework.annotations.Enable;
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
        assertEquals(
            Set.of(
                "cache",
                "task",
                "testEnable",
                EnableAttributeRegistryTest.class.getName()
            ),
            app.enableFunctions()
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
