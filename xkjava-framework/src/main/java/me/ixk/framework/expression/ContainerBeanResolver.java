/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import io.github.imsejin.expression.AccessException;
import io.github.imsejin.expression.BeanResolver;
import io.github.imsejin.expression.EvaluationContext;
import me.ixk.framework.ioc.XkJava;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:12
 */
public class ContainerBeanResolver implements BeanResolver {

    private final XkJava app;

    public ContainerBeanResolver(XkJava app) {
        this.app = app;
    }

    @Override
    public Object resolve(EvaluationContext context, String beanName)
        throws AccessException {
        return this.app.make(beanName);
    }
}
