/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.DispatchServletException;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.registry.after.ExceptionHandlerRegistry;
import me.ixk.framework.registry.after.InitBinderRegistry;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ParameterNameDiscoverer;
import me.ixk.framework.web.MethodParameter;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.RequestParameterResolver;
import me.ixk.framework.web.RequestParametersPostResolver;
import me.ixk.framework.web.ResponseReturnValueResolver;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(
        ControllerHandler.class
    );

    private final Class<?> controllerClass;
    private final Method method;

    private final XkJava app;

    private static final String NO_RESOLVER = "NO_RESOLVER";

    public ControllerHandler(final Method handler) {
        this.controllerClass = handler.getDeclaringClass();
        this.method = handler;
        this.app = XkJava.of();
    }

    @Override
    public Object handle(final Request request) {
        // 将控制器信息注入 RequestContext
        this.app.setAttribute(
                "controllerClass",
                this.controllerClass,
                ScopeType.REQUEST
            );
        this.app.setAttribute(
                "controllerMethod",
                this.method,
                ScopeType.REQUEST
            );
        try {
            final WebDataBinder webDataBinder = new WebDataBinder(
                this.app,
                request
            );
            final Object controller =
                this.app.make(this.controllerClass, webDataBinder);
            this.processInitBinder(webDataBinder);
            return this.callMethodHandler(
                    controller,
                    this.method,
                    this.app.make(WebResolverRegistry.class),
                    this.app.make(WebContext.class),
                    webDataBinder
                );
        } catch (final Throwable e) {
            log.error("ControllerHandler Exception", e);
            // 处理 ExceptionHandler 注解定义的错误处理器
            final Object result = this.processException(e);
            if (NO_RESOLVER.equals(result)) {
                // 若错误未能解决，或者产生了新的错误则向上抛出
                throw new Exception(e);
            }
            return result;
        }
    }

    protected void processInitBinder(final WebDataBinder binder) {
        final Map<String, Object> args = new ConcurrentHashMap<>();
        args.put("binder", binder);
        args.put("webDataBinder", binder);
        args.put("dataBinder", binder);
        args.put(WebDataBinder.class.getName(), binder);
        args.put(DataBinder.class.getName(), binder);
        final InitBinderRegistry registry =
            this.app.make(InitBinderRegistry.class);
        final InitBinderHandlerResolver resolver = registry
            .getControllerResolvers()
            .get(this.controllerClass);
        if (resolver != null) {
            for (final Method method : resolver.resolveMethods()) {
                this.app.call(this.controllerClass, method, Object.class, args);
            }
        }
        for (final InitBinderHandlerResolver handlerResolver : registry.getAdviceResolvers()) {
            for (final Method method : handlerResolver.resolveMethods()) {
                this.app.call(this.controllerClass, method, Object.class, args);
            }
        }
    }

    protected Object processException(final Throwable exception) {
        Object result = NO_RESOLVER;
        final ExceptionHandlerRegistry registry =
            this.app.make(ExceptionHandlerRegistry.class);
        final ExceptionHandlerResolver resolver = registry
            .getControllerResolvers()
            .get(this.controllerClass);
        if (resolver != null) {
            result =
                this.processException(
                        exception,
                        this.controllerClass,
                        resolver
                    );
            if (!result.equals(NO_RESOLVER)) {
                return result;
            }
        }
        for (final Map.Entry<Class<?>, ExceptionHandlerResolver> entry : registry
            .getAdviceResolvers()
            .entrySet()) {
            result =
                this.processException(
                        exception,
                        entry.getKey(),
                        entry.getValue()
                    );
            if (!result.equals(NO_RESOLVER)) {
                return result;
            }
        }

        return result;
    }

    protected Object processException(
        final Throwable exception,
        final Class<?> clazz,
        final ExceptionHandlerResolver resolver
    ) {
        try {
            final Method method = resolver.resolveMethod(exception);
            if (method != null) {
                // 绑定可能注入的异常
                final Map<String, Object> args = new ConcurrentHashMap<>();
                args.put("exception", exception);
                args.put(exception.getClass().getName(), exception);
                args.put(Throwable.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                // 获取返回值
                return this.app.call(clazz, method, Object.class, args);
            }
        } catch (final Throwable e) {
            throw new DispatchServletException(
                "Process ExceptionHandlerResolver failed",
                e
            );
        }
        return NO_RESOLVER;
    }

    protected Object callMethodHandler(
        final Object controller,
        final Method method,
        final WebResolverRegistry registry,
        final WebContext context,
        final WebDataBinder binder
    )
        throws java.lang.Exception {
        Object[] dependencies = new Object[method.getParameterCount()];
        final Parameter[] parameters = method.getParameters();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            method
        );
        final MergedAnnotation methodAnnotation = AnnotationUtils.getAnnotation(
            method
        );
        final MethodParameter methodParameter = new MethodParameter(
            controller,
            this.controllerClass,
            method,
            parameters,
            parameterNames,
            methodAnnotation
        );
        for (final RequestParameterResolver resolver : registry.getRequestParameterResolvers()) {
            for (int i = 0; i < parameters.length; i++) {
                methodParameter.setParameterIndex(i);
                if (
                    resolver.supportsParameter(dependencies[i], methodParameter)
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
        for (final RequestParametersPostResolver resolver : registry.getRequestParametersPostResolvers()) {
            if (resolver.supportsParameters(dependencies, methodParameter)) {
                dependencies =
                    resolver.resolveParameters(
                        dependencies,
                        methodParameter,
                        context,
                        binder
                    );
            }
        }
        // call
        ReflectUtil.setAccessible(method);
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
        Object returnValue = method.invoke(
            ClassUtil.isStatic(method) ? null : controller,
            dependencies
        );
        final MethodReturnValue methodReturnValue = new MethodReturnValue(
            controller,
            this.controllerClass,
            method,
            methodAnnotation
        );
        for (final ResponseReturnValueResolver resolver : registry.getResponseReturnValueResolvers()) {
            if (resolver.supportsReturnType(returnValue, methodReturnValue)) {
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
}
