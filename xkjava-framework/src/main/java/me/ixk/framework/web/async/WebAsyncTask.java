package me.ixk.framework.web.async;

import java.util.concurrent.Callable;
import java.util.function.Function;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.task.AsyncTaskExecutor;
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

    private AsyncTaskExecutor executor;

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
        final AsyncTaskExecutor executor,
        final Callable<V> callable
    ) {
        this(callable);
        this.executor = executor;
        this.timeout = timeout;
    }

    public Callable<V> getCallable() {
        return callable;
    }

    @Nullable
    public Long getTimeout() {
        return timeout;
    }

    public AsyncTaskExecutor getExecutor() {
        if (executor != null) {
            return executor;
        } else if (executorName != null) {
            final AsyncTaskExecutor taskExecutor = XkJava
                .of()
                .make(executorName, AsyncTaskExecutor.class);
            if (taskExecutor != null) {
                executor = taskExecutor;
                return executor;
            }
        }
        return null;
    }

    public String getExecutorName() {
        return executorName;
    }

    public Callable<V> getTimeoutCallback() {
        return timeoutCallback;
    }

    public Function<Throwable, V> getErrorCallback() {
        return errorCallback;
    }

    public Runnable getCompletionCallback() {
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
            public <T> Object handleTimeout(Request request, Callable<T> task)
                throws Exception {
                return WebAsyncTask.this.timeoutCallback != null
                    ? WebAsyncTask.this.timeoutCallback.call()
                    : CallableInterceptor.RESULT_NONE;
            }

            @Override
            public <T> Object handleError(
                Request request,
                Callable<T> task,
                Throwable t
            ) throws Exception {
                return WebAsyncTask.this.errorCallback != null
                    ? WebAsyncTask.this.errorCallback.apply(t)
                    : CallableInterceptor.RESULT_NONE;
            }

            @Override
            public <T> void afterCompletion(Request request, Callable<T> task)
                throws Exception {
                if (WebAsyncTask.this.completionCallback != null) {
                    WebAsyncTask.this.completionCallback.run();
                }
            }
        };
    }
}
