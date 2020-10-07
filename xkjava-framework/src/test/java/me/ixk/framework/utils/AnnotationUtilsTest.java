/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import me.ixk.framework.test.XkJavaRunner;
import me.ixk.framework.utils.annotation.Parent;
import me.ixk.framework.utils.entity.Test1;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(XkJavaRunner.class)
class AnnotationUtilsTest {

    @Test
    void getTypesAnnotatedAndInherit() {
        Set<Class<?>> list = AnnotationUtils.getTypesAnnotated(Parent.class);
        assertEquals(list.size(), 2);
    }

    @Test
    void getAnnotationInherit() {
        Parent parent = AnnotationUtils.getParentAnnotation(
            Test1.class,
            Parent.class
        );
        assertNotNull(parent);
    }
}
