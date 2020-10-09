/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import me.ixk.framework.test.XkJavaRunner;
import me.ixk.framework.utils.annotation.Parent;
import me.ixk.framework.utils.annotation.SuperParent;
import me.ixk.framework.utils.entity.FalseConditional;
import me.ixk.framework.utils.entity.Test1;
import me.ixk.framework.utils.entity.TrueConditional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(XkJavaRunner.class)
class AnnotationUtilsTest {

    @Test
    void getTypesAnnotated() {
        Set<Class<?>> list = AnnotationUtils.getTypesAnnotated(Parent.class);
        assertEquals(list.size(), 3);
    }

    @Test
    void getAnnotation() {
        MergeAnnotation parent = AnnotationUtils.getAnnotation(
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

    @Test
    void isCondition() {
        assertTrue(AnnotationUtils.isCondition(TrueConditional.class));
        assertFalse(AnnotationUtils.isCondition(FalseConditional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterConditionAnnotation() {
        Set<Class<?>> classes = ReflectionsUtils
            .make(this.getClass().getPackageName())
            .getTypesAnnotatedWith(Parent.class);
        Set<Class<?>> set = AnnotationUtils.filterConditionAnnotation(classes);
        assertSame(classes.size(), set.size() + 1);
    }
}
