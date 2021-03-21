package me.ixk.framework.web.async;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import me.ixk.framework.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.annotation.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 10:53
 */
public class WebDeferredTask<V> {

    private static final Logger log = LoggerFactory.getLogger(
        WebDeferredTask.class
    );
    private static final Object RESULT_NONE = new Object();

    @Nullable
    private Long timeout;

    private Callable<?> timeoutCallback;

    private Consumer<Throwable> errorCallback;

    private Runnable completionCallback;

    private WebDeferredHandler deferredHandler;

    private volatile Object result = RESULT_NONE;

    public WebDeferredTask() {}

    public WebDeferredTask(@Nullable Long timeout) {
        this.timeout = timeout;
    }

    public boolean hasResult() {
        return (this.result != RESULT_NONE);
    }

    public Object result() {
        Object resultToCheck = this.result;
        return (resultToCheck != RESULT_NONE ? resultToCheck : null);
    }

    @Nullable
    public Long timeout() {
        return timeout;
    }

    public void timeout(@Nullable Long timeout) {
        this.timeout = timeout;
    }

    public WebDeferredTask<V> onTimeout(Callable<?> callback) {
        this.timeoutCallback = callback;
        return this;
    }

    public WebDeferredTask<V> onError(Consumer<Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }

    public WebDeferredTask<V> onCompletion(Runnable callback) {
        this.completionCallback = callback;
        return this;
    }

    public boolean result(V result) {
        return resultInner(result);
    }

    public boolean complete() {
        return resultInner(null);
    }

    public boolean resultInner(Object result) {
        WebDeferredHandler deferredHandler;
        synchronized (this) {
            this.result = result;
            deferredHandler = this.deferredHandler;
            if (deferredHandler == null) {
                return true;
            }
            this.deferredHandler = null;
        }
        deferredHandler.handle(result);
        return true;
    }

    public final void handler(WebDeferredHandler deferredHandler) {
        Object resultToHandle;
        synchronized (this) {
            resultToHandle = this.result;
            if (resultToHandle == RESULT_NONE) {
                this.deferredHandler = deferredHandler;
                return;
            }
        }
        try {
            deferredHandler.handle(resultToHandle);
        } catch (Throwable ex) {
            log.debug("Failed to process async result", ex);
        }
    }

    final DeferredInterceptor getInterceptor() {
        return new DeferredInterceptor() {
            @Override
            public <S> boolean handleTimeout(
                Request request,
                WebDeferredTask<S> deferredResult
            ) throws Exception {
                if (timeoutCallback != null) {
                    final Object value = timeoutCallback.call();
                    if (value != RESULT_NONE) {
                        try {
                            resultInner(value);
                        } catch (Throwable ex) {
                            log.debug("Failed to handle timeout result", ex);
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public <S> boolean handleError(
                Request request,
                WebDeferredTask<S> deferredResult,
                Throwable t
            ) {
                try {
                    if (errorCallback != null) {
                        errorCallback.accept(t);
                    }
                } finally {
                    try {
                        resultInner(t);
                    } catch (Throwable ex) {
                        log.debug("Failed to handle error result", ex);
                    }
                }
                return false;
            }

            @Override
            public <S> void afterCompletion(
                Request request,
                WebDeferredTask<S> deferredResult
            ) {
                if (completionCallback != null) {
                    completionCallback.run();
                }
            }
        };
    }

    @FunctionalInterface
    public interface WebDeferredHandler {
        /**
         * 设置值后的回调
         *
         * @param result 回调值
         */
        void handle(Object result);
    }
}
