package me.ixk.framework.web.resolver;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.result.AsyncResult;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebAsyncTask;

/**
 * 异步任务解析器
 *
 * @author Otstar Lin
 * @date 2021/1/11 下午 9:13
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class AsyncTaskResponseConvertResolver
    implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        return value instanceof WebAsyncTask || value instanceof AsyncResult;
    }

    @Override
    public boolean resolveConvert(
        final Object value,
        final WebContext context,
        final RouteInfo info
    ) {
        final WebAsyncTask<?> asyncTask;
        if (value instanceof WebAsyncTask) {
            asyncTask = (WebAsyncTask<?>) value;
        } else if (value instanceof AsyncResult) {
            asyncTask =
                new WebAsyncTask<>(
                    () -> ((AsyncResult<?>) value).handle(context)
                );
        } else {
            return false;
        }
        context.getAsyncManager().startAsync(asyncTask);
        return true;
    }
}
