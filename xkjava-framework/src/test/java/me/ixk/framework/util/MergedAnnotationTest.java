package me.ixk.framework.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ixk.framework.util.annotation.Children1;
import me.ixk.framework.util.annotation.Parent;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2021/1/15 下午 3:45
 */
@Children1
class MergedAnnotationTest {

    @Test
    void from() {
        final MergedAnnotation annotation = MergedAnnotation.from(
            MergedAnnotationTest.class
        );
        final Parent parent = annotation.getAnnotation(Parent.class);
        assertEquals("123", parent.name());
        assertEquals("123", parent.value());
    }
}
