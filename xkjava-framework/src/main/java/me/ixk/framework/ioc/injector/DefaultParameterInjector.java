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
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的参数注入器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:44
 */
public class DefaultParameterInjector implements ParameterInjector {
    private static final Logger log = LoggerFactory.getLogger(
        DefaultParameterInjector.class
    );

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
            MergedAnnotation annotation = AnnotationUtils.getAnnotation(
                parameter
            );
            DataBind dataBind = annotation.getAnnotation(DataBind.class);
            dependencies[i] =
                dataBinder.getObject(
                    parameterName,
                    parameter.getType(),
                    annotation
                );
            if (
                dependencies[i] == null &&
                dataBind != null &&
                dataBind.required()
            ) {
                final NullPointerException exception = new NullPointerException(
                    "Target [" +
                    method.getDeclaringClass().getName() +
                    "@" +
                    method.getName() +
                    "(" +
                    parameterName +
                    ")] is required, but inject value is null"
                );
                log.error(
                    "Target [{}@{}({})] is required, but inject value is null",
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    parameterName
                );
                throw exception;
            }
        }
        return dependencies;
    }
}
