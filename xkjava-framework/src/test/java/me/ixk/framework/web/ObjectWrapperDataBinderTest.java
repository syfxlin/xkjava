/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.entity.User;
import me.ixk.framework.ioc.ObjectWrapperDataBinder;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import me.ixk.framework.utils.AnnotationUtils;
import org.junit.jupiter.api.Test;

@XkJavaTest
class ObjectWrapperDataBinderTest {

    @Test
    void getObject() {
        final Map<String, String> map = Map.of(
            "name",
            "Otstar Lin",
            "age",
            "20",
            "user.name",
            "syfxlin",
            "user.age",
            "21",
            "user.user.name",
            "syfxlin1",
            "&user.user.age",
            "22"
        );
        final ObjectWrapperDataBinder dataBinder = new ObjectWrapperDataBinder(
            XkJava.of(),
            List.of(map::get)
        );
        final DataBind dataBind = new DataBind() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return DataBind.class;
            }

            @Override
            public String value() {
                return "user";
            }

            @Override
            public String name() {
                return "user";
            }

            @Override
            public boolean required() {
                return false;
            }
        };
        final User user = dataBinder.getObject(
            "user",
            User.class,
            AnnotationUtils.wrapAnnotation(dataBind)
        );
        System.out.println(user);
    }
}
