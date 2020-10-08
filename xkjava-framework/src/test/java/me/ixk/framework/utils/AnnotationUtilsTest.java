/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import me.ixk.framework.test.XkJavaRunner;
import me.ixk.framework.utils.annotation.Parent;
import me.ixk.framework.utils.annotation.SuperParent;
import me.ixk.framework.utils.entity.Test1;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(XkJavaRunner.class)
class AnnotationUtilsTest {

    @Test
    void getTypesAnnotated() {
        Set<Class<?>> list = AnnotationUtils.getTypesAnnotated(Parent.class);
        assertEquals(list.size(), 2);
    }

    @Test
    void getAnnotation() {
        SuperParent parent = AnnotationUtils.getAnnotation(
            Test1.class,
            SuperParent.class
        );
        assertNotNull(parent);
    }

    @Test
    void getAnnotationValues() {
        MultiValueMap<String, Object> map = AnnotationUtils.getAnnotationValues(
            Test1.class,
            SuperParent.class
        );
        assertNotNull(map);
    }
}
