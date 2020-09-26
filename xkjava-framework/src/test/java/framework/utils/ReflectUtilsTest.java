/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package framework.utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.utils.ReflectUtils;
import org.junit.jupiter.api.Test;

class ReflectUtilsTest {

    @Test
    void sortMethod() {
        Constructor<?>[] constructors =
            SortClass.class.getDeclaredConstructors();
        ReflectUtils.sortConstructors(constructors);
        System.out.println(
            Arrays
                .stream(constructors)
                .map(Constructor::getParameterCount)
                .collect(Collectors.toList())
        );
    }

    public static class SortClass {
        private int a1;
        private int a2;
        private int a3;
        private int a4;
        private int a5;

        public SortClass() {}

        @Autowired
        public SortClass(int a1) {
            this.a1 = a1;
        }

        public SortClass(int a1, int a2) {
            this.a1 = a1;
            this.a2 = a2;
        }

        @Autowired
        public SortClass(int a1, int a2, int a3) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
        }

        public SortClass(int a1, int a2, int a3, int a4) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
            this.a4 = a4;
        }

        public SortClass(int a1, int a2, int a3, int a4, int a5) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
            this.a4 = a4;
            this.a5 = a5;
        }
    }
}
