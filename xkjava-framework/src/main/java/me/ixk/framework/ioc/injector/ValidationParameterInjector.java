/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import javax.validation.Valid;
import me.ixk.framework.exceptions.ValidException;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;
import me.ixk.framework.utils.Validation;

public class ValidationParameterInjector implements ParameterInjector {

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
        int validResultIndex = -1;
        int validGroupIndex = -1;
        ValidResult<Object> validResult = null;
        ValidGroup validGroup = null;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames[i];
            if (parameter.getType() == ValidResult.class) {
                validResultIndex = i;
            } else if (parameter.getType() == ValidGroup.class) {
                validGroupIndex = i;
            } else {
                Valid valid = AnnotationUtils.getAnnotation(
                    parameter,
                    Valid.class
                );
                if (valid != null) {
                    validResult = Validation.validate(dependencies[i]);
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
                dependencies[validResultIndex] = validResult;
                isThrow = false;
            }
            if (validGroupIndex != -1) {
                dependencies[validGroupIndex] = validGroup;
                isThrow = false;
            }
            if (isThrow && validResult.isFail()) {
                throw new ValidException(validGroup);
            }
        }
        return dependencies;
    }
}
