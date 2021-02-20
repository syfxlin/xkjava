package me.ixk.framework.web.resolver;

import me.ixk.framework.annotation.Order;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebDeferredTask;

/**
 * @author Otstar Lin
 * @date 2021/1/12 下午 9:04
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class DeferredTaskResponseReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof WebDeferredTask;
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        if (value instanceof WebDeferredTask) {
            context.getAsyncManager().startDeferred((WebDeferredTask<?>) value);
            return null;
        }
        return value;
    }
}
