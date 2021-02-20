/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.lang.reflect.Parameter;
import javax.validation.Valid;
import me.ixk.framework.annotation.Order;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.exception.ValidException;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ValidGroup;
import me.ixk.framework.util.ValidResult;
import me.ixk.framework.util.Validation;
import me.ixk.framework.web.MethodParameter;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;

/**
 * 验证参数解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:17
 */
@WebResolver
@Order(Order.LOWEST_PRECEDENCE)
public class ValidationParametersResolver
    implements RequestParametersPostResolver {

    @Override
    public boolean supportsParameters(
        final Object[] parameters,
        final MethodParameter parameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        for (final MergedAnnotation annotation : parameter.getParameterAnnotations()) {
            if (annotation.hasAnnotation(Valid.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] resolveParameters(
        final Object[] values,
        final MethodParameter methodParameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        int validResultIndex = -1;
        int validGroupIndex = -1;
        ValidResult<Object> validResult = null;
        ValidGroup validGroup = null;
        final Parameter[] parameters = methodParameter.getParameters();
        final String[] parameterNames = methodParameter.getParameterNames();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final String parameterName = parameterNames[i];
            if (parameter.getType() == ValidResult.class) {
                validResultIndex = i;
            } else if (parameter.getType() == ValidGroup.class) {
                validGroupIndex = i;
            } else {
                final boolean valid = MergedAnnotation.has(
                    parameter,
                    Valid.class
                );
                if (valid) {
                    validResult = Validation.validate(values[i]);
                    if (validGroup == null) {
                        validGroup = new ValidGroup();
                    }
                    validGroup.addValidResult(parameterName, validResult);
                }
            }
        }
        if (validResult != null) {
            boolean isThrow = true;
            if (validResultIndex != -1) {
                values[validResultIndex] = validResult;
                isThrow = false;
            }
            if (validGroupIndex != -1) {
                values[validGroupIndex] = validGroup;
                isThrow = false;
            }
            if (isThrow && validResult.isFail()) {
                throw new ValidException(validGroup);
            }
        }
        return values;
    }
}
