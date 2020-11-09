/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Parameter;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.annotations.Injector;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.ParameterContext;
import me.ixk.framework.ioc.ParameterContext.ParameterEntry;
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
@Injector
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultParameterInjector implements ParameterInjector {
    private static final Logger log = LoggerFactory.getLogger(
        DefaultParameterInjector.class
    );

    @Override
    public boolean supportsParameter(
        ParameterContext context,
        Object[] dependencies
    ) {
        return dependencies.length > 0;
    }

    @Override
    public Object[] inject(
        Container container,
        Object[] dependencies,
        ParameterContext context,
        DataBinder dataBinder
    ) {
        final ParameterEntry[] entries = context.getParameterEntries();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isChanged()) {
                continue;
            }
            Parameter parameter = entries[i].getElement();
            String parameterName = entries[i].getName();
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
                    context.getExecutable().getDeclaringClass().getName() +
                    "@" +
                    context.getExecutable().getName() +
                    "(" +
                    parameterName +
                    ")] is required, but inject value is null"
                );
                log.error(
                    "Target [{}@{}({})] is required, but inject value is null",
                    context.getExecutable().getDeclaringClass().getName(),
                    context.getExecutable().getName(),
                    parameterName
                );
                throw exception;
            }
            entries[i].setChanged(true);
        }
        return dependencies;
    }
}
