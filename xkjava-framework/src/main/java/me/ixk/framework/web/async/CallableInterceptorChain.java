package me.ixk.framework.web.async;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import me.ixk.framework.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2021/1/10 下午 7:05
 */
public class CallableInterceptorChain {

    private static final Logger log = LoggerFactory.getLogger(
        CallableInterceptorChain.class
    );
    private final List<CallableInterceptor> interceptors;

    private volatile Future<?> taskFuture;

    public CallableInterceptorChain(
        final List<CallableInterceptor> interceptors
    ) {
        this.interceptors = interceptors;
    }

    public Future<?> getTaskFuture() {
        return taskFuture;
    }

    public void setTaskFuture(final Future<?> taskFuture) {
        this.taskFuture = taskFuture;
    }

    public void applyBeforeProcess(
        final Request request,
        final Callable<?> task
    ) throws Exception {
        for (final CallableInterceptor interceptor : this.interceptors) {
            interceptor.beforeProcess(request, task);
        }
    }

    public Object applyAfterProcess(
        final Request request,
        final Callable<?> task,
        final Object concurrentResult
    ) {
        Throwable exceptionResult = null;
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            try {
                this.interceptors.get(i)
                    .afterProcess(request, task, concurrentResult);
            } catch (final Throwable ex) {
                // Save the first exception but invoke all interceptors
                if (exceptionResult != null) {
                    if (log.isTraceEnabled()) {
                        log.trace(
                            "Ignoring failure in afterProcess method",
                            ex
                        );
                    }
                } else {
                    exceptionResult = ex;
                }
            }
        }
        return (exceptionResult != null) ? exceptionResult : concurrentResult;
    }

    public Object triggerAfterTimeout(
        final Request request,
        final Callable<?> task
    ) {
        cancelTask();
        for (final CallableInterceptor interceptor : this.interceptors) {
            try {
                final Object result = interceptor.handleTimeout(request, task);
                if (result != CallableInterceptor.RESULT_NONE) {
                    return result;
                }
            } catch (final Throwable ex) {
                return ex;
            }
        }
        return CallableInterceptor.RESULT_NONE;
    }

    private void cancelTask() {
        final Future<?> future = this.taskFuture;
        if (future != null) {
            try {
                future.cancel(true);
            } catch (final Throwable ex) {
                // Ignore
            }
        }
    }

    public Object triggerAfterError(
        final Request request,
        final Callable<?> task,
        final Throwable throwable
    ) {
        cancelTask();
        for (final CallableInterceptor interceptor : this.interceptors) {
            try {
                final Object result = interceptor.handleError(
                    request,
                    task,
                    throwable
                );
                if (result != CallableInterceptor.RESULT_NONE) {
                    return result;
                }
            } catch (final Throwable ex) {
                return ex;
            }
        }
        return CallableInterceptor.RESULT_NONE;
    }

    public void triggerAfterCompletion(
        final Request request,
        final Callable<?> task
    ) {
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            try {
                this.interceptors.get(i).afterCompletion(request, task);
            } catch (final Throwable ex) {
                if (log.isTraceEnabled()) {
                    log.trace("Ignoring failure in afterCompletion method", ex);
                }
            }
        }
    }
}
