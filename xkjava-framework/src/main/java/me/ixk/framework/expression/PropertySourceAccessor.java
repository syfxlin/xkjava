/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import io.github.imsejin.expression.AccessException;
import io.github.imsejin.expression.EvaluationContext;
import io.github.imsejin.expression.PropertyAccessor;
import io.github.imsejin.expression.TypedValue;
import me.ixk.framework.property.PropertySource;

/**
 * @author Otstar Lin
 * @date 2020/12/21 下午 11:30
 */
public class PropertySourceAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[] { PropertySource.class };
    }

    @Override
    public boolean canRead(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        return true;
    }

    @Override
    public TypedValue read(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        return new TypedValue(((PropertySource<?>) target).get(name));
    }

    @Override
    public boolean canWrite(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        return true;
    }

    @Override
    public void write(
        EvaluationContext context,
        Object target,
        String name,
        Object newValue
    ) throws AccessException {
        ((PropertySource<?>) target).set(name, newValue);
    }
}
