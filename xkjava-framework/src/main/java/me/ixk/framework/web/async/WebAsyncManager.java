package me.ixk.framework.web.async;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.task.AsyncTaskExecutor;
import me.ixk.framework.task.SimpleAsyncTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2021/1/10 下午 3:51
 */
@Component(name = "webAsyncManager")
@Scope(ScopeType.REQUEST)
public class WebAsyncManager {

    private static final Logger log = LoggerFactory.getLogger(
        WebAsyncManager.class
    );
    private static final Object RESULT_NONE = new Object();
    private static final AsyncTaskExecutor DEFAULT_TASK_EXECUTOR = new SimpleAsyncTaskExecutor(
        WebAsyncManager.class.getSimpleName()
    );

    private Request request;
    private AsyncTaskExecutor executor = DEFAULT_TASK_EXECUTOR;
    private volatile Object concurrentResult = RESULT_NONE;
    private final Map<Object, CallableInterceptor> callableInterceptors = new LinkedHashMap<>();
    private final Map<Object, DeferredInterceptor> deferredInterceptors = new LinkedHashMap<>();

    @Deprecated
    public WebAsyncManager() {
        // only use cglib
    }

    public WebAsyncManager(final Request request) {
        this.request = request;
    }

    public void setAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    public void registerCallableInterceptor(
        final Object key,
        final CallableInterceptor interceptor
    ) {
        this.callableInterceptors.put(key, interceptor);
    }

    public void registerCallableInterceptors(
        final CallableInterceptor... interceptors
    ) {
        for (final CallableInterceptor interceptor : interceptors) {
            final String key =
                interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.callableInterceptors.put(key, interceptor);
        }
    }

    public CallableInterceptor getCallableInterceptor(final Object key) {
        return this.callableInterceptors.get(key);
    }

    public void registerDeferredInterceptor(
        final Object key,
        final DeferredInterceptor interceptor
    ) {
        this.deferredInterceptors.put(key, interceptor);
    }

    public void registerDeferredInterceptors(
        final DeferredInterceptor... interceptors
    ) {
        for (final DeferredInterceptor interceptor : interceptors) {
            final String key =
                interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.deferredInterceptors.put(key, interceptor);
        }
    }

    public DeferredInterceptor getDeferredInterceptor(final Object key) {
        return this.deferredInterceptors.get(key);
    }

    public boolean hasConcurrentResult() {
        return (this.concurrentResult != RESULT_NONE);
    }

    public Object getConcurrentResult() {
        return this.concurrentResult;
    }

    public boolean isConcurrentHandlingStarted() {
        return (this.request != null && this.request.isAsyncStarted());
    }

    public void clearConcurrentResult() {
        synchronized (WebAsyncManager.this) {
            this.concurrentResult = RESULT_NONE;
        }
    }

    public void startAsync(final Callable<?> callable) {
        this.startAsync(new WebAsyncTask<>(callable));
    }

    public void startAsync(final WebAsyncTask<?> webAsyncTask) {
        final Long timeout = webAsyncTask.getTimeout();
        if (timeout != null) {
            this.request.setTimeout(timeout);
        }
        final AsyncTaskExecutor executor = webAsyncTask.getExecutor();
        if (executor != null) {
            this.executor = executor;
        }

        final List<CallableInterceptor> interceptors = new ArrayList<>();
        interceptors.add(webAsyncTask.getInterceptor());
        interceptors.addAll(this.callableInterceptors.values());

        final Callable<?> callable = webAsyncTask.getCallable();
        final CallableInterceptorChain interceptorChain = new CallableInterceptorChain(
            interceptors
        );
        this.request.addTimeoutHandler(
                () -> {
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "Async request timeout for " + formatRequestUri()
                        );
                    }
                    final Object result = interceptorChain.triggerAfterTimeout(
                        this.request,
                        callable
                    );
                    if (result != CallableInterceptor.RESULT_NONE) {
                        setConcurrentResultAndDispatch(result);
                    }
                }
            );
        this.request.addErrorHandler(
                ex -> {
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "Async request error for " +
                            formatRequestUri() +
                            ": " +
                            ex
                        );
                    }
                    Object result = interceptorChain.triggerAfterError(
                        this.request,
                        callable,
                        ex
                    );
                    result =
                        (
                            result != CallableInterceptor.RESULT_NONE
                                ? result
                                : ex
                        );
                    setConcurrentResultAndDispatch(result);
                }
            );
        this.request.addCompletionHandler(
                () ->
                    interceptorChain.triggerAfterCompletion(
                        this.request,
                        callable
                    )
            );

        this.startAsyncProcessing();
        try {
            final Future<?> future =
                this.executor.submit(
                        () -> {
                            Object result = null;
                            try {
                                interceptorChain.applyBeforeProcess(
                                    this.request,
                                    callable
                                );
                                result = callable.call();
                            } catch (final Throwable ex) {
                                result = ex;
                            } finally {
                                result =
                                    interceptorChain.applyAfterProcess(
                                        this.request,
                                        callable,
                                        result
                                    );
                            }
                            setConcurrentResultAndDispatch(result);
                        }
                    );
            interceptorChain.setTaskFuture(future);
        } catch (final RejectedExecutionException ex) {
            final Object result = interceptorChain.applyAfterProcess(
                this.request,
                callable,
                ex
            );
            setConcurrentResultAndDispatch(result);
            throw ex;
        }
    }

    public void startDeferred(final WebDeferredTask<?> webDeferredTask) {
        final Long timeout = webDeferredTask.getTimeout();
        if (timeout != null) {
            this.request.setTimeout(timeout);
        }

        List<DeferredInterceptor> interceptors = new ArrayList<>();
        interceptors.add(webDeferredTask.getInterceptor());
        interceptors.addAll(this.deferredInterceptors.values());

        final DeferredInterceptorChain interceptorChain = new DeferredInterceptorChain(
            interceptors
        );
        this.request.addTimeoutHandler(
                () -> {
                    try {
                        interceptorChain.triggerAfterTimeout(
                            this.request,
                            webDeferredTask
                        );
                    } catch (Throwable ex) {
                        setConcurrentResultAndDispatch(ex);
                    }
                }
            );
        this.request.addErrorHandler(
                ex -> {
                    try {
                        if (
                            !interceptorChain.triggerAfterError(
                                this.request,
                                webDeferredTask,
                                ex
                            )
                        ) {
                            return;
                        }
                        webDeferredTask.setResultInternal(ex);
                    } catch (Throwable interceptorEx) {
                        setConcurrentResultAndDispatch(interceptorEx);
                    }
                }
            );
        this.request.addCompletionHandler(
                () ->
                    interceptorChain.triggerAfterCompletion(
                        this.request,
                        webDeferredTask
                    )
            );
        this.startAsyncProcessing();
        try {
            interceptorChain.applyBeforeProcess(this.request, webDeferredTask);
            webDeferredTask.setResultHandler(
                result -> {
                    result =
                        interceptorChain.applyAfterProcess(
                            this.request,
                            webDeferredTask,
                            result
                        );
                    setConcurrentResultAndDispatch(result);
                }
            );
        } catch (Throwable ex) {
            setConcurrentResultAndDispatch(ex);
        }
    }

    private void startAsyncProcessing() {
        synchronized (this) {
            this.concurrentResult = RESULT_NONE;
        }
        this.request.startAsync();

        if (log.isDebugEnabled()) {
            log.debug("Started async request");
        }
    }

    private String formatRequestUri() {
        return request != null ? request.getRequestURI() : "servlet container";
    }

    private void setConcurrentResultAndDispatch(final Object result) {
        synchronized (WebAsyncManager.this) {
            if (this.concurrentResult != RESULT_NONE) {
                return;
            }
            this.concurrentResult = result;
        }
        if (this.request.isAsyncComplete()) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "Async result set but request already complete: " +
                    formatRequestUri()
                );
            }
            return;
        }
        if (log.isDebugEnabled()) {
            final boolean isError = result instanceof Throwable;
            log.debug(
                "Async " +
                (isError ? "error" : "result set") +
                ", dispatch to " +
                formatRequestUri()
            );
        }
        this.request.dispatch();
    }
}
