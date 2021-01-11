package me.ixk.framework.web.async;

import me.ixk.framework.http.Request;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 11:19
 */
public interface DeferredInterceptor {
    default <T> void beforeProcess(Request request, WebDeferredTask<T> task)
        throws Exception {}

    default <T> void afterProcess(
        Request request,
        WebDeferredTask<T> task,
        Object concurrentResult
    ) throws Exception {}

    default <T> boolean handleTimeout(Request request, WebDeferredTask<T> task)
        throws Exception {
        return true;
    }

    default <T> boolean handleError(
        Request request,
        WebDeferredTask<T> task,
        Throwable t
    ) throws Exception {
        return true;
    }

    default <T> void afterCompletion(Request request, WebDeferredTask<T> task)
        throws Exception {}
}
