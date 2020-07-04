/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.ioc.BeanBeforeProcessor;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;

public class PostConstructProcessor
    extends AbstractBeanProcessor
    implements BeanBeforeProcessor {

    public PostConstructProcessor(Container container) {
        super(container);
    }

    @Override
    public Object process(Object instance, Binding binding) {
        Method method = binding.getInitMethod();
        if (method != null) {
            container.call(instance, method, Object.class);
        }
        return instance;
    }
}
