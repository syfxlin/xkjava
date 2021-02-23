package me.ixk.framework.web.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.XkJava;
import reactor.util.annotation.Nullable;

/**
 * Web 异步任务
 *
 * @author Otstar Lin
 * @date 2021/1/10 下午 5:29
 */
public class WebAsyncTask<V> {

    private final Callable<V> callable;

    @Nullable
    private Long timeout;

    private ExecutorService executor;

    private String executorName;

    private Callable<V> timeoutCallback;

    private Function<Throwable, V> errorCallback;

    private Runnable completionCallback;

    public WebAsyncTask(final Callable<V> callable) {
        this.callable = callable;
    }

    public WebAsyncTask(final long timeout, final Callable<V> callable) {
        this(callable);
        this.timeout = timeout;
    }

    public WebAsyncTask(
        @Nullable final Long timeout,
        final String executorName,
        final Callable<V> callable
    ) {
        this(callable);
        this.executorName = executorName;
        this.timeout = timeout;
    }

    public WebAsyncTask(
        @Nullable final Long timeout,
        final ExecutorService executor,
        final Callable<V> callable
    ) {
        this(callable);
        this.executor = executor;
        this.timeout = timeout;
    }

    public Callable<V> callable() {
        return callable;
    }

    @Nullable
    public Long timeout() {
        return timeout;
    }

    public void timeout(@Nullable final Long timeout) {
        this.timeout = timeout;
    }

    public ExecutorService getExecutor() {
        if (executor != null) {
            return executor;
        } else if (executorName != null) {
            final ExecutorService taskExecutor = XkJava
                .of()
                .make(executorName, ExecutorService.class);
            if (taskExecutor != null) {
                executor = taskExecutor;
                return executor;
            }
        }
        return null;
    }

    public void setExecutor(final ExecutorService executor) {
        this.executor = executor;
    }

    public void setExecutorName(final String executorName) {
        this.executorName = executorName;
    }

    public String getExecutorName() {
        return executorName;
    }

    public Callable<V> timeoutCallback() {
        return timeoutCallback;
    }

    public Function<Throwable, V> errorCallback() {
        return errorCallback;
    }

    public Runnable completionCallback() {
        return completionCallback;
    }

    public WebAsyncTask<V> onTimeout(final Callable<V> callback) {
        this.timeoutCallback = callback;
        return this;
    }

    public WebAsyncTask<V> onError(final Function<Throwable, V> callback) {
        this.errorCallback = callback;
        return this;
    }

    public WebAsyncTask<V> onCompletion(final Runnable callback) {
        this.completionCallback = callback;
        return this;
    }

    CallableInterceptor getInterceptor() {
        return new CallableInterceptor() {
            @Override
            public <T> Object handleTimeout(
                final Request request,
                final Callable<T> task
            ) throws Exception {
                return timeoutCallback != null
                    ? timeoutCallback.call()
                    : CallableInterceptor.RESULT_NONE;
            }

            @Override
            public <T> Object handleError(
                final Request request,
                final Callable<T> task,
                final Throwable t
            ) throws Exception {
                return errorCallback != null
                    ? errorCallback.apply(t)
                    : CallableInterceptor.RESULT_NONE;
            }

            @Override
            public <T> void afterCompletion(
                final Request request,
                final Callable<T> task
            ) throws Exception {
                if (completionCallback != null) {
                    completionCallback.run();
                }
            }
        };
    }
}
