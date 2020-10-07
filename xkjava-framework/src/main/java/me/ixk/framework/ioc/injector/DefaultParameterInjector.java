/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.utils.AnnotationUtils;

public class DefaultParameterInjector implements ParameterInjector {

    @Override
    public Object[] inject(
        Container container,
        Binding binding,
        Executable method,
        Parameter[] parameters,
        String[] parameterNames,
        Object[] dependencies,
        DataBinder dataBinder
    ) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames[i];
            DataBind dataBind = AnnotationUtils.getParentAnnotation(
                parameter,
                DataBind.class
            );
            dependencies[i] =
                dataBinder.getObject(
                    parameterName,
                    parameter.getType(),
                    dataBind
                );
            if (
                dependencies[i] == null &&
                dataBind != null &&
                dataBind.required()
            ) {
                throw new NullPointerException(
                    "Target [" +
                    method.getDeclaringClass().getName() +
                    "@" +
                    method.getName() +
                    "(" +
                    parameterName +
                    ")] is required, but inject value is null"
                );
            }
        }
        return dependencies;
    }
}
