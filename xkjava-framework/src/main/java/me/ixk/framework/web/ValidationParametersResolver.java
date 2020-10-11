/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Parameter;
import javax.validation.Valid;
import me.ixk.framework.exceptions.ValidException;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;
import me.ixk.framework.utils.Validation;

public class ValidationParametersResolver
    implements RequestParametersPostResolver {

    @Override
    public boolean supportsParameters(
        Object[] parameters,
        MethodParameter parameter
    ) {
        for (MergedAnnotation annotation : parameter.getParameterAnnotations()) {
            if (annotation.hasAnnotation(Valid.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] resolveParameters(
        Object[] values,
        MethodParameter methodParameter,
        WebContext context,
        WebDataBinder binder
    ) {
        int validResultIndex = -1;
        int validGroupIndex = -1;
        ValidResult<Object> validResult = null;
        ValidGroup validGroup = null;
        Parameter[] parameters = methodParameter.getParameters();
        String[] parameterNames = methodParameter.getParameterNames();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames[i];
            if (parameter.getType() == ValidResult.class) {
                validResultIndex = i;
            } else if (parameter.getType() == ValidGroup.class) {
                validGroupIndex = i;
            } else {
                boolean valid = AnnotationUtils.hasAnnotation(
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
