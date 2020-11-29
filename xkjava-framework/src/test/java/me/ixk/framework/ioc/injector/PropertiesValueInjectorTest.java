/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.config.AppProperties;
import me.ixk.framework.config.DatabaseProperties;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 1:00
 */
@XkJavaTest
class PropertiesValueInjectorTest {

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    AppProperties appProperties;

    @Autowired
    PropertySourceProperties propertySourceProperties;

    @Autowired
    OnlyValueProperties onlyValueProperties;

    @Test
    void configurationProperties() {
        assertNotNull(databaseProperties.getUrl());
        assertNotNull(databaseProperties.getUsername());
        assertNotNull(databaseProperties.getPassword());
        assertNotNull(databaseProperties.getDriver());
    }

    @Test
    void defaultValue() {
        assertNotEquals(0, appProperties.getPort());
    }

    @Test
    void propertySource() {
        assertTrue(propertySourceProperties.isTest());
        assertTrue(propertySourceProperties.isValue());
        assertTrue(propertySourceProperties.isValueEl());
        assertTrue(propertySourceProperties.isTest1());
        assertEquals("test3", propertySourceProperties.getTest3());
        assertFalse(propertySourceProperties.isSkip());
    }

    @Test
    void onlyValue() {
        assertNotNull(onlyValueProperties.getUrl());
    }
}
