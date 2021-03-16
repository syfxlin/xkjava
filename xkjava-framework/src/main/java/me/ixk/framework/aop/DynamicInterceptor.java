/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.aop.AspectHandler.TargetInfo;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 切面拦截器
 * <p>
 * 用于 Cglib 拦截对象
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:31
 */
public class DynamicInterceptor implements MethodInterceptor, CanGetTarget {

    protected final TargetSource targetSource;

    public DynamicInterceptor(final TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    @Override
    public Object intercept(
        final Object object,
        final Method method,
        final Object[] args,
        final MethodProxy methodProxy
    ) throws Throwable {
        // 匹配切面
        final List<Advice> advices = this.targetSource.getAdvices(method);
        final Object target = this.targetSource.getTarget();
        if (advices != null && !advices.isEmpty()) {
            final AspectHandler handler = new AspectHandler(
                new TargetInfo(
                    target,
                    this.targetSource.getTargetType(),
                    method,
                    methodProxy,
                    args
                ),
                0,
                advices
            );
            return handler.invokeAspect();
        }

        return methodProxy.invoke(target, args);
    }

    @Override
    public Object getTarget() {
        return this.targetSource.getTarget();
    }
}
