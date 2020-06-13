package me.ixk.framework.kernel;

import static me.ixk.framework.ioc.RequestContext.currentAttributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.DispatchServletException;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.RequestContext;
import me.ixk.framework.middleware.Handler;

public class ControllerHandler implements Handler {
    private Class<?> controllerClass;
    private final String methodName;

    private final Application app;

    private static final String NO_RESOLVER = "NO_RESOLVER";

    public ControllerHandler(String handler) {
        String[] handlerArr = handler.split("@");
        try {
            this.controllerClass = Class.forName(handlerArr[0]);
        } catch (ClassNotFoundException e) {
            this.controllerClass = null;
        }
        this.methodName = handlerArr[1];
        this.app = Application.get();
    }

    @Override
    public Object handle(Request request) {
        // 将控制器信息注入 RequestContext
        RequestContext context = RequestContext.currentAttributes();
        context.setHandler(this.controllerClass, this.methodName);
        try {
            Object controller = this.app.make(this.controllerClass);
            this.app.setGlobalArgs(request.all());
            Object result =
                this.app.call(controller, this.methodName, Object.class);
            this.app.resetGlobalArgs();
            return result;
        } catch (Throwable e) {
            // 处理 ExceptionHandler 注解定义的错误处理器
            Object result = this.processException(e, request);
            if (NO_RESOLVER.equals(result)) {
                // 若错误未能解决，或者产生了新的错误则向上抛出
                throw e;
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    protected Object processException(Throwable exception, Request request) {
        Map<Class<?>, ExceptionHandlerResolver> controllerResolvers =
            this.app.getAttribute(
                    "controllerExceptionHandlerResolvers",
                    Map.class
                );
        if (
            controllerResolvers.containsKey(
                currentAttributes().getControllerType()
            )
        ) {
            Object result =
                this.processException(
                        exception,
                        request,
                        controllerResolvers.entrySet()
                    );
            if (!result.equals(NO_RESOLVER)) {
                return result;
            }
        }
        Map<Class<?>, ExceptionHandlerResolver> handlerResolvers =
            this.app.getAttribute("adviceExceptionHandlerResolvers", Map.class);
        return this.processException(
                exception,
                request,
                handlerResolvers.entrySet()
            );
    }

    protected Object processException(
        Throwable exception,
        Request request,
        Set<Map.Entry<Class<?>, ExceptionHandlerResolver>> entrySet
    ) {
        for (Map.Entry<Class<?>, ExceptionHandlerResolver> entry : entrySet) {
            try {
                Method method = entry.getValue().resolveMethod(exception);
                if (method != null) {
                    // 绑定可能注入的异常
                    Map<String, Object> args = new ConcurrentHashMap<>();
                    args.put("exception", exception);
                    args.put(exception.getClass().getName(), exception);
                    args.put(Throwable.class.getName(), exception);
                    args.put(java.lang.Exception.class.getName(), exception);
                    args.put(Exception.class.getName(), exception);
                    // 获取返回值
                    return this.app.call(
                            entry.getKey(),
                            method,
                            Object.class,
                            args
                        );
                }
            } catch (Throwable e) {
                throw new DispatchServletException(
                    "Process ExceptionHandlerResolver failed",
                    e
                );
            }
        }
        return NO_RESOLVER;
    }
}
