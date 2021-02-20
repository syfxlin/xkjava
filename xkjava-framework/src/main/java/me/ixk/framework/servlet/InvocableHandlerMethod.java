package me.ixk.framework.servlet;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.binder.DataBinder;
import me.ixk.framework.ioc.binder.DefaultDataBinder;
import me.ixk.framework.registry.after.InitBinderRegistry;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.util.Convert;
import me.ixk.framework.web.ExceptionInfo;
import me.ixk.framework.web.MethodParameter;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;
import me.ixk.framework.web.resolver.HandlerExceptionResolver;
import me.ixk.framework.web.resolver.InitBinderHandlerResolver;
import me.ixk.framework.web.resolver.RequestParameterResolver;
import me.ixk.framework.web.resolver.RequestParametersPostResolver;
import me.ixk.framework.web.resolver.ResponseReturnValueResolver;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 3:53
 */
public class InvocableHandlerMethod extends HandlerMethod {

    private final XkJava app;
    private final WebResolverRegistry webResolverRegistry;
    private final InitBinderRegistry initBinderRegistry;

    public InvocableHandlerMethod(
        final XkJava app,
        final HandlerMethod handler
    ) {
        super(handler);
        this.app = app;
        this.webResolverRegistry = this.app.make(WebResolverRegistry.class);
        this.initBinderRegistry = this.app.make(InitBinderRegistry.class);
    }

    public Object invokeForRequest(
        final Request request,
        final Response response
    ) throws Exception {
        // 基础依赖
        final WebDataBinder dataBinder = new WebDataBinder(this.app, request);
        final WebContext webContext = this.app.make(WebContext.class);
        // 获取真实的 Handler
        final Object handler = this.getRealHandler(dataBinder);
        // 处理 InitBinder 方法
        try {
            this.processInitBinder(handler, dataBinder);
            final Object[] dependencies =
                this.processParameterResolver(
                        this.createMethodParameter(handler),
                        webContext,
                        dataBinder
                    );
            final Object returnValue = this.doInvoke(handler, dependencies);
            return this.processReturnValueResolver(
                    returnValue,
                    this.createMethodReturnValue(handler),
                    webContext
                );
        } catch (Throwable th) {
            final Object result =
                this.processException(
                        th,
                        this.createExceptionInfo(handler, request, response),
                        webContext,
                        dataBinder
                    );
            if (HandlerExceptionResolver.NO_RESOLVER == result) {
                // 若错误未能解决，或者产生了新的错误则向上抛出
                throw th;
            }
            return this.processReturnValueResolver(
                    result,
                    this.createMethodReturnValue(handler),
                    webContext
                );
        }
    }

    private Object doInvoke(final Object handler, final Object... dependencies)
        throws Exception {
        final Method method = this.getMethod();
        ReflectUtil.setAccessible(method);
        try {
            return method.invoke(
                ClassUtil.isStatic(method) ? null : handler,
                dependencies
            );
        } catch (final InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else if (targetException instanceof Error) {
                throw (Error) targetException;
            } else if (targetException instanceof Exception) {
                throw (Exception) targetException;
            } else {
                throw new IllegalStateException(
                    "Invocation failure",
                    targetException
                );
            }
        }
    }

    private Object getRealHandler(final WebDataBinder dataBinder) {
        final Object handler = this.getHandler();
        final Class<?> handlerType = this.getHandlerType();
        if (handler instanceof String) {
            return this.app.make((String) handler, handlerType, dataBinder);
        } else if (handler instanceof Class) {
            return this.app.make((Class<?>) handler, dataBinder);
        }
        return handler;
    }

    private MethodParameter createMethodParameter(final Object handler) {
        return new MethodParameter(handler, this.getMethod());
    }

    private MethodReturnValue createMethodReturnValue(final Object handler) {
        return new MethodReturnValue(
            handler,
            this.getHandlerType(),
            this.getMethod()
        );
    }

    private ExceptionInfo createExceptionInfo(
        final Object handler,
        final Request request,
        final Response response
    ) {
        return new ExceptionInfo(
            handler,
            this.getHandlerType(),
            this.getMethod(),
            request,
            response
        );
    }

    private void processInitBinder(
        final Object handler,
        final WebDataBinder dataBinder
    ) {
        final Map<String, Object> args = new ConcurrentHashMap<>(10);
        args.put("binder", dataBinder);
        args.put("webDataBinder", dataBinder);
        args.put("dataBinder", dataBinder);
        args.put(WebDataBinder.class.getName(), dataBinder);
        args.put(DataBinder.class.getName(), dataBinder);
        final InitBinderHandlerResolver resolver = initBinderRegistry
            .getControllerResolvers()
            .get(this.getHandlerType());
        if (resolver != null) {
            for (final Method method : resolver.resolveMethods()) {
                this.app.call(
                        handler,
                        method,
                        new DefaultDataBinder(this.app, args)
                    );
            }
        }
        for (final InitBinderHandlerResolver handlerResolver : initBinderRegistry.getAdviceResolvers()) {
            for (final Method method : handlerResolver.resolveMethods()) {
                this.app.call(
                        handler,
                        method,
                        new DefaultDataBinder(this.app, args)
                    );
            }
        }
    }

    private Object[] processParameterResolver(
        final MethodParameter methodParameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        final Parameter[] parameters = methodParameter.getParameters();
        Object[] dependencies = new Object[parameters.length];
        final Class<?>[] parameterTypes = methodParameter
            .getMethod()
            .getParameterTypes();
        for (final RequestParameterResolver resolver : webResolverRegistry.getRequestParameterResolvers()) {
            for (int i = 0; i < parameters.length; i++) {
                methodParameter.setParameterIndex(i);
                if (
                    resolver.supportsParameter(
                        dependencies[i],
                        methodParameter,
                        context,
                        binder
                    )
                ) {
                    dependencies[i] =
                        resolver.resolveParameter(
                            dependencies[i],
                            methodParameter,
                            context,
                            binder
                        );
                }
            }
        }
        methodParameter.setParameterIndex(-1);
        for (final RequestParametersPostResolver resolver : webResolverRegistry.getRequestParametersPostResolvers()) {
            if (
                resolver.supportsParameters(
                    dependencies,
                    methodParameter,
                    context,
                    binder
                )
            ) {
                dependencies =
                    resolver.resolveParameters(
                        dependencies,
                        methodParameter,
                        context,
                        binder
                    );
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            if (null == dependencies[i]) {
                dependencies[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
            } else if (
                !parameterTypes[i].isAssignableFrom(dependencies[i].getClass())
            ) {
                final Object targetValue = Convert.convert(
                    parameterTypes[i],
                    dependencies[i]
                );
                if (null != targetValue) {
                    dependencies[i] = targetValue;
                }
            }
        }
        return dependencies;
    }

    private Object processReturnValueResolver(
        Object returnValue,
        final MethodReturnValue methodReturnValue,
        final WebContext context
    ) {
        for (final ResponseReturnValueResolver resolver : webResolverRegistry.getResponseReturnValueResolvers()) {
            if (
                resolver.supportsReturnType(
                    returnValue,
                    methodReturnValue,
                    context
                )
            ) {
                returnValue =
                    resolver.resolveReturnValue(
                        returnValue,
                        methodReturnValue,
                        context
                    );
            }
        }
        return returnValue;
    }

    private Object processException(
        final Throwable exception,
        final ExceptionInfo exceptionInfo,
        final WebContext context,
        final WebDataBinder dataBinder
    ) {
        for (final HandlerExceptionResolver resolver : webResolverRegistry.getHandlerExceptionResolvers()) {
            final Object result = resolver.resolveException(
                exception,
                exceptionInfo,
                context,
                dataBinder
            );
            if (HandlerExceptionResolver.NO_RESOLVER != result) {
                return result;
            }
        }
        return HandlerExceptionResolver.NO_RESOLVER;
    }
}
