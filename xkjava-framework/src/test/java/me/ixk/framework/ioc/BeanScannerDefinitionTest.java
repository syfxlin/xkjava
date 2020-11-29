/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.annotations.ComponentScan.Filter;
import me.ixk.framework.annotations.FilterType;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 3:42
 */
@XkJavaTest
class BeanScannerDefinitionTest {

    @Autowired
    XkJava app;

    @Test
    void excludeFilters() {
        final BeanScannerDefinition definition = new BeanScannerDefinition(
            new BeanScanner(app),
            Exclude.class.getAnnotation(ComponentScan.class)
        );
        assertFalse(
            definition.getFilter().test("me.ixk.framework.ioc.SkipComponent")
        );
        assertFalse(
            definition
                .getFilter()
                .test("me.ixk.framework.ioc.SkipComponentType")
        );
        assertFalse(
            definition
                .getFilter()
                .test("me.ixk.framework.ioc.SkipComponentRegex")
        );
        assertTrue(definition.getFilter().test("me.ixk.framework.ioc.XkJava"));
    }

    @Test
    void includeFilters() {
        final BeanScannerDefinition definition = new BeanScannerDefinition(
            new BeanScanner(app),
            Include.class.getAnnotation(ComponentScan.class)
        );
        assertTrue(
            definition.getFilter().test("me.ixk.framework.ioc.SkipComponent")
        );
        assertTrue(
            definition
                .getFilter()
                .test("me.ixk.framework.ioc.SkipComponentType")
        );
        assertTrue(
            definition
                .getFilter()
                .test("me.ixk.framework.ioc.SkipComponentRegex")
        );
        assertFalse(definition.getFilter().test("me.ixk.framework.ioc.XkJava"));
    }

    @ComponentScan(
        value = "me.ixk.framework",
        excludeFilters = {
            @Filter(classes = Skip.class),
            @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SkipComponentType.class
            ),
            @Filter(
                type = FilterType.REGEX,
                pattern = "me.ixk.framework.ioc.SkipComponentR.*"
            ),
        }
    )
    private static class Exclude {}

    @ComponentScan(
        value = "me.ixk.framework",
        includeFilters = {
            @Filter(classes = Skip.class),
            @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SkipComponentType.class
            ),
            @Filter(
                type = FilterType.REGEX,
                pattern = "me.ixk.framework.ioc.SkipComponentR.*"
            ),
        }
    )
    private static class Include {}
}
