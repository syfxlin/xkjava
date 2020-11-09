/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.lang.reflect.Method;
import me.ixk.framework.annotations.BeanProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.BeanBeforeProcessor;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;

/**
 * 构造器后处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:07
 */
@BeanProcessor
@Order(Order.HIGHEST_PRECEDENCE)
public class PostConstructProcessor implements BeanBeforeProcessor {

    @Override
    public Object process(
        Container container,
        Binding binding,
        Object instance
    ) {
        Method method = binding.getInitMethod();
        if (method != null) {
            container.call(instance, method, Object.class);
        }
        return instance;
    }
}
