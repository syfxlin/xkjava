package me.ixk.framework.web.async;

import java.util.List;
import me.ixk.framework.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 11:23
 */
public class DeferredInterceptorChain {

    private static final Logger log = LoggerFactory.getLogger(
        DeferredInterceptorChain.class
    );
    private final List<DeferredInterceptor> interceptors;

    public DeferredInterceptorChain(List<DeferredInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void applyBeforeProcess(
        final Request request,
        final WebDeferredTask<?> task
    ) throws Exception {
        for (final DeferredInterceptor interceptor : this.interceptors) {
            interceptor.beforeProcess(request, task);
        }
    }

    public Object applyAfterProcess(
        Request request,
        WebDeferredTask<?> task,
        Object concurrentResult
    ) {
        try {
            for (int i = this.interceptors.size() - 1; i >= 0; i--) {
                this.interceptors.get(i)
                    .afterProcess(request, task, concurrentResult);
            }
        } catch (Throwable ex) {
            return ex;
        }
        return concurrentResult;
    }

    public void triggerAfterTimeout(Request request, WebDeferredTask<?> task)
        throws Exception {
        for (DeferredInterceptor interceptor : this.interceptors) {
            if (!interceptor.handleTimeout(request, task)) {
                break;
            }
        }
    }

    public boolean triggerAfterError(
        Request request,
        WebDeferredTask<?> task,
        Throwable ex
    ) throws Exception {
        for (DeferredInterceptor interceptor : this.interceptors) {
            if (!interceptor.handleError(request, task, ex)) {
                return false;
            }
        }
        return true;
    }

    public void triggerAfterCompletion(
        Request request,
        WebDeferredTask<?> task
    ) {
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            try {
                this.interceptors.get(i).afterCompletion(request, task);
            } catch (Throwable ex) {
                log.trace("Ignoring failure in afterCompletion method", ex);
            }
        }
    }
}
