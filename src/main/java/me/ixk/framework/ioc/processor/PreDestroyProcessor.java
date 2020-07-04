/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.ioc.BeanAfterProcessor;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;

public class PreDestroyProcessor
    extends AbstractBeanProcessor
    implements BeanAfterProcessor {

    public PreDestroyProcessor(Container container) {
        super(container);
    }

    @Override
    public Object process(Object instance, Binding binding) {
        Method method = binding.getDestroyMethod();
        if (method != null) {
            container.call(instance, method, Object.class);
        }
        return instance;
    }
}
