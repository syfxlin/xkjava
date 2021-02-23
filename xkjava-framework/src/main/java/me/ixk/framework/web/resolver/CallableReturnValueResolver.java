/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.util.concurrent.Callable;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.WebAsync;
import me.ixk.framework.annotation.web.WebResolver;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebAsyncTask;

/**
 * 可调用返回值解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:41
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class CallableReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof Callable;
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        final WebAsyncTask<?> asyncTask = new WebAsyncTask<>(
            (Callable<?>) value
        );
        final WebAsync webAsync = returnValue
            .getMethodAnnotation()
            .getAnnotation(WebAsync.class);
        if (webAsync != null && !webAsync.value().isEmpty()) {
            asyncTask.setExecutorName(webAsync.value());
        }
        context.async().startAsync(asyncTask);
        return null;
    }
}
