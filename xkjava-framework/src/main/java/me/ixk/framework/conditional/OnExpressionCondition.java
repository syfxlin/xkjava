/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.conditional;

import cn.hutool.core.convert.Convert;
import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.Condition;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Express;
import me.ixk.framework.utils.MergeAnnotation;

public class OnExpressionCondition implements Condition {

    @Override
    public boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergeAnnotation annotation
    ) {
        return Convert.convert(
            Boolean.class,
            Express.executeEnv(annotation.get("value"))
        );
    }
}
