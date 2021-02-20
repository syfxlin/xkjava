package me.ixk.framework.web.resolver;

import me.ixk.framework.annotation.Order;
import me.ixk.framework.annotation.WebAsync;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.http.result.AsyncResult;
import me.ixk.framework.web.MethodReturnValue;
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
public class AsyncTaskResponseReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof WebAsyncTask || value instanceof AsyncResult;
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
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
            return value;
        }
        final WebAsync webAsync = returnValue
            .getMethodAnnotation()
            .getAnnotation(WebAsync.class);
        if (webAsync != null && !webAsync.value().isEmpty()) {
            asyncTask.setExecutorName(webAsync.value());
        }
        context.getAsyncManager().startAsync(asyncTask);

        return null;
    }
}
