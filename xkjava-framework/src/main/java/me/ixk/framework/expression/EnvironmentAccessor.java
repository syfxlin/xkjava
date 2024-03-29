/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import io.github.imsejin.expression.EvaluationContext;
import io.github.imsejin.expression.PropertyAccessor;
import io.github.imsejin.expression.TypedValue;
import me.ixk.framework.property.Environment;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:10
 */
public class EnvironmentAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[] { Environment.class };
    }

    @Override
    public boolean canRead(
        EvaluationContext context,
        Object target,
        String name
    ) {
        return true;
    }

    @Override
    public TypedValue read(
        EvaluationContext context,
        Object target,
        String name
    ) {
        return new TypedValue(((Environment) target).get(name));
    }

    @Override
    public boolean canWrite(
        EvaluationContext context,
        Object target,
        String name
    ) {
        return true;
    }

    @Override
    public void write(
        EvaluationContext context,
        Object target,
        String name,
        Object newValue
    ) {
        ((Environment) target).set(name, newValue);
    }
}
