package me.ixk.framework.servlet;

import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.Parameter;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.exception.ResponseException;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.util.Convert;
import me.ixk.framework.web.ExceptionInfo;
import me.ixk.framework.web.MethodParameter;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;
import me.ixk.framework.web.resolver.AfterHandlerExceptionResolver;
import me.ixk.framework.web.resolver.HandlerExceptionResolver;
import me.ixk.framework.web.resolver.RequestParameterResolver;
import me.ixk.framework.web.resolver.RequestParametersPostResolver;
import me.ixk.framework.web.resolver.ResponseConvertResolver;
import me.ixk.framework.web.resolver.ResponseReturnValueResolver;

/**
 * @author Otstar Lin
 * @date 2021/3/21 下午 4:01
 */
@Component(name = "handlerProcessor")
public class HandlerProcessor {

    private final WebResolverRegistry webResolverRegistry;

    public HandlerProcessor(WebResolverRegistry webResolverRegistry) {
        this.webResolverRegistry = webResolverRegistry;
    }

    public Object[] processParameterResolver(
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

    public Object processReturnValueResolver(
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

    public Object processException(
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

    public void processConvertResolver(
        final Object returnValue,
        final WebContext context,
        final RouteInfo info
    ) {
        for (final ResponseConvertResolver converter : webResolverRegistry.getResponseConverters()) {
            if (converter.supportsConvert(returnValue, context, info)) {
                if (converter.resolveConvert(returnValue, context, info)) {
                    return;
                }
            }
        }
        throw new ResponseException(
            "The return value cannot be converted into a response. [" +
            returnValue.getClass() +
            "]"
        );
    }

    public boolean processAfterException(
        final Throwable exception,
        final WebContext context
    ) {
        for (final AfterHandlerExceptionResolver resolver : webResolverRegistry.getAfterHandlerExceptionResolvers()) {
            final boolean resolved = resolver.resolveException(
                exception,
                context
            );
            if (resolved) {
                return true;
            }
        }
        return false;
    }
}
