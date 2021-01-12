package me.ixk.framework.web.resolver;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebDeferredTask;

/**
 * @author Otstar Lin
 * @date 2021/1/12 下午 9:04
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class DeferredTaskResponseConvertResolver
    implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        return value instanceof WebDeferredTask;
    }

    @Override
    public boolean resolveConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        if (value instanceof WebDeferredTask) {
            context.getAsyncManager().startDeferred((WebDeferredTask<?>) value);
            return true;
        }
        return false;
    }
}
