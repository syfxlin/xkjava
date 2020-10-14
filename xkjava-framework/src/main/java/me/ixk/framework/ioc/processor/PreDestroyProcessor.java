/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.ioc.BeanAfterProcessor;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;

/**
 * 销毁前处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:07
 */
public class PreDestroyProcessor implements BeanAfterProcessor {

    @Override
    public Object process(
        Container container,
        Binding binding,
        Object instance
    ) {
        Method method = binding.getDestroyMethod();
        if (method != null) {
            container.call(instance, method, Object.class);
        }
        return instance;
    }
}
