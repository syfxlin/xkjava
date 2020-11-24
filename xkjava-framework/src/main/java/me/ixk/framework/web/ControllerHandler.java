/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.registry.after.InitBinderRegistry;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.route.RouteResult;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ParameterNameDiscoverer;
import me.ixk.framework.web.RequestAttributeRegistry.RequestAttributeDefinition;
import me.ixk.framework.web.resolver.InitBinderHandlerResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制器执行器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:12
 */
public class ControllerHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(
        ControllerHandler.class
    );
    private static final String NO_RESOLVER = "NO_RESOLVER";

    private final XkJava app;
    private final WebResolverRegistry registry;

    private final Class<?> controllerClass;
    private final Method method;
    private final MergedAnnotation methodAnnotation;
    private Object controller;
    private WebDataBinder dataBinder;
    private WebContext context;

    public ControllerHandler(final Method handler) {
        this.controllerClass = handler.getDeclaringClass();
        this.method = handler;
        this.methodAnnotation = AnnotationUtils.getAnnotation(this.method);
        this.app = XkJava.of();
        this.registry = this.app.make(WebResolverRegistry.class);
    }

    @Override
    public void before(
        final RouteResult result,
        final Request request,
        final Response response
    ) {
        final RequestAttributeRegistry registry =
            this.app.make(RequestAttributeRegistry.class);
        final Map<String, RequestAttributeDefinition> definitionMap = registry.getRegistry(
            result.getHandler().getMethod()
        );
        if (definitionMap != null) {
            for (final Entry<String, RequestAttributeDefinition> entry : definitionMap.entrySet()) {
                final String attributeName = entry.getKey();
                final RequestAttributeDefinition definition = entry.getValue();
                request.setAttribute(
                    attributeName,
                    definition
                        .getRegistry()
                        .register(
                            this.app,
                            attributeName,
                            definition.getMethod(),
                            definition.getAnnotation()
                        )
                );
            }
        }
        // 将控制器信息注入 RequestContext
        this.app.setAttribute(
                "me.ixk.framework.web.ControllerHandler.controllerClass",
                this.controllerClass,
                ScopeType.REQUEST
            );
        this.app.setAttribute(
                "me.ixk.framework.web.ControllerHandler.controllerMethod",
                this.method,
                ScopeType.REQUEST
            );
        // 创建 WebContext
        this.context = this.app.make(WebContext.class);
        // 创建 WebDataBinder
        this.dataBinder = new WebDataBinder(this.app, request);
        // 创建/获取 控制器
        this.controller = this.app.make(this.controllerClass, this.dataBinder);
        // 执行 @InitBinder 标记的方法
        this.processInitBinder();
    }

    @Override
    public Object handle(final Request request, final Response response) {
        try {
            // 实际调用路由方法
            return this.callHandler();
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            log.error("ControllerHandler Exception", e);
            // 处理 ExceptionHandler 注解定义的错误处理器
            final Object result = this.processException(e, request, response);
            if (NO_RESOLVER.equals(result)) {
                // 若错误未能解决，或者产生了新的错误则向上抛出
                throw new Exception(e);
            }
            return this.processReturnValueResolver(
                    result,
                    this.controller,
                    this.methodAnnotation,
                    this.registry,
                    this.context
                );
        }
    }

    private void processInitBinder() {
        final Map<String, Object> args = new ConcurrentHashMap<>(10);
        args.put("binder", this.dataBinder);
        args.put("webDataBinder", this.dataBinder);
        args.put("dataBinder", this.dataBinder);
        args.put(WebDataBinder.class.getName(), this.dataBinder);
        args.put(DataBinder.class.getName(), this.dataBinder);
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

    private Object processException(
        final Throwable exception,
        final Request request,
        final Response response
    ) {
        final ExceptionInfo info = new ExceptionInfo(
            this.controller,
            this.controllerClass,
            this.method,
            this.methodAnnotation,
            request,
            response
        );
        for (final HandlerExceptionResolver resolver : registry.getHandlerExceptionResolvers()) {
            final Object result = resolver.resolveException(
                exception,
                info,
                context,
                dataBinder
            );
            if (!NO_RESOLVER.equals(result)) {
                return result;
            }
        }
        return NO_RESOLVER;
    }

    private Object callHandler() throws java.lang.Exception {
        final Object[] dependencies =
            this.processParameterResolver(
                    controller,
                    methodAnnotation,
                    registry,
                    context,
                    dataBinder
                );
        // call
        ReflectUtil.setAccessible(method);
        final Object returnValue = method.invoke(
            ClassUtil.isStatic(method) ? null : controller,
            dependencies
        );
        return this.processReturnValueResolver(
                returnValue,
                controller,
                methodAnnotation,
                registry,
                context
            );
    }

    private Object[] processParameterResolver(
        final Object controller,
        final MergedAnnotation methodAnnotation,
        final WebResolverRegistry registry,
        final WebContext context,
        final WebDataBinder binder
    ) {
        Object[] dependencies = new Object[method.getParameterCount()];
        final Parameter[] parameters = method.getParameters();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
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
        final Object controller,
        final MergedAnnotation methodAnnotation,
        final WebResolverRegistry registry,
        final WebContext context
    ) {
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
