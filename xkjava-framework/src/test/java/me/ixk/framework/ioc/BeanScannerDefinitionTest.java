/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.ixk.framework.annotation.Autowired;
import me.ixk.framework.annotation.ComponentScan;
import me.ixk.framework.annotation.ComponentScan.Filter;
import me.ixk.framework.annotation.FilterType;
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
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponent.class")
        );
        assertFalse(
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponentType.class")
        );
        assertFalse(
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponentRegex.class")
        );
        assertTrue(
            definition.getFilter().test("me/ixk/framework/ioc/XkJava.class")
        );
    }

    @Test
    void includeFilters() {
        final BeanScannerDefinition definition = new BeanScannerDefinition(
            new BeanScanner(app),
            Include.class.getAnnotation(ComponentScan.class)
        );
        assertTrue(
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponent.class")
        );
        assertTrue(
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponentType.class")
        );
        assertTrue(
            definition
                .getFilter()
                .test("me/ixk/framework/ioc/SkipComponentRegex.class")
        );
        assertFalse(
            definition.getFilter().test("me/ixk/framework/ioc/XkJava.class")
        );
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
