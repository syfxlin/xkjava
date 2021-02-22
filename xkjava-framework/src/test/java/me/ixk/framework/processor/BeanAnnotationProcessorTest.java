/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import static org.junit.jupiter.api.Assertions.assertFalse;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.ComponentScan;
import me.ixk.framework.annotation.core.ComponentScan.Filter;
import me.ixk.framework.annotation.core.FilterType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 2:19
 */
@XkJavaTest
@ComponentScan(
    excludeFilters = {
        @Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = ImportTarget.class
        ),
    }
)
class BeanAnnotationProcessorTest {

    @Autowired
    XkJava app;

    @Test
    void processImport() {
        assertFalse(app.has(ImportTarget.class));
    }
}
