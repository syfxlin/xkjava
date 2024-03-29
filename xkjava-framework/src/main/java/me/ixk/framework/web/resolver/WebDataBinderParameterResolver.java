/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.ixk.framework.annotation.core.DataBind;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.WebResolver;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ValidGroup;
import me.ixk.framework.util.ValidResult;
import me.ixk.framework.web.MethodParameter;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;

/**
 * 数据绑定器解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:17
 */
@WebResolver
@Order(Order.LOWEST_PRECEDENCE)
public class WebDataBinderParameterResolver
    implements RequestParameterResolver {

    private static final Set<Class<?>> SKIP_TYPES = new HashSet<>(
        Arrays.asList(ValidResult.class, ValidGroup.class)
    );

    @Override
    public boolean supportsParameter(
        final Object value,
        final MethodParameter parameter,
        WebContext context,
        WebDataBinder binder
    ) {
        if (SKIP_TYPES.contains(parameter.getParameterType())) {
            return false;
        }
        return value == null;
    }

    @Override
    public Object resolveParameter(
        final Object value,
        final MethodParameter parameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        final MergedAnnotation annotation = MergedAnnotation.from(
            parameter.getParameter()
        );
        final DataBind dataBind = annotation.getAnnotation(DataBind.class);
        final Object dependency = binder.getObject(
            parameter.getParameterName(),
            TypeWrapper.forParameter(parameter.getParameter()),
            annotation,
            context.app()
        );
        if (dependency == null && dataBind != null && dataBind.required()) {
            throw new NullPointerException(
                "Target [" +
                parameter.getMethod().getDeclaringClass().getName() +
                "@" +
                parameter.getMethod().getName() +
                "(" +
                parameter.getParameterName() +
                ")] is required, but inject value is null"
            );
        }
        return dependency;
    }
}
