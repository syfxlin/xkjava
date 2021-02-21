/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import cn.hutool.core.util.ReflectUtil;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotation.DataBind;
import me.ixk.framework.entity.User;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.binder.ObjectWrapperDataBinder;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.test.XkJavaTest;
import me.ixk.framework.util.MergedAnnotation;
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
            List.of(map::get)
        );
        final User user = dataBinder.getObject(
            "user",
            TypeWrapper.forClass(User.class),
            MergedAnnotation.from(
                ReflectUtil.getMethod(this.getClass(), "method", String.class)
            ),
            XkJava.of()
        );
        System.out.println(user);
    }

    void method(@DataBind(name = "user") String name) {}
}
