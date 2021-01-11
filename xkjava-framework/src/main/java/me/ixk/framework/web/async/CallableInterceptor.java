package me.ixk.framework.web.async;

import java.util.concurrent.Callable;
import me.ixk.framework.http.Request;

/**
 * @author Otstar Lin
 * @date 2021/1/10 下午 7:04
 */
public interface CallableInterceptor {
    Object RESULT_NONE = new Object();

    default <T> void beforeProcess(Request request, Callable<T> task)
        throws Exception {}

    default <T> void afterProcess(
        Request request,
        Callable<T> task,
        Object concurrentResult
    ) throws Exception {}

    default <T> Object handleTimeout(Request request, Callable<T> task)
        throws Exception {
        return RESULT_NONE;
    }

    default <T> Object handleError(
        Request request,
        Callable<T> task,
        Throwable t
    ) throws Exception {
        return RESULT_NONE;
    }

    default <T> void afterCompletion(Request request, Callable<T> task)
        throws Exception {}
}
