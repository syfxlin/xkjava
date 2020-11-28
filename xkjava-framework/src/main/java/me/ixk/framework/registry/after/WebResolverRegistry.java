/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.web.AfterHandlerExceptionResolver;
import me.ixk.framework.web.HandlerExceptionResolver;
import me.ixk.framework.web.RequestParameterResolver;
import me.ixk.framework.web.RequestParametersPostResolver;
import me.ixk.framework.web.ResponseReturnValueResolver;

/**
 * WebResolverRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:55
 */
@Component(name = "webHandlerRegistry")
public class WebResolverRegistry implements AfterBeanRegistry {

    private final List<RequestParameterResolver> requestParameterResolvers = new ArrayList<>();
    private final List<ResponseReturnValueResolver> responseReturnValueResolvers = new ArrayList<>();
    private final List<RequestParametersPostResolver> requestParametersPostResolvers = new ArrayList<>();
    private final List<HandlerExceptionResolver> handlerExceptionResolvers = new ArrayList<>();
    private final List<AfterHandlerExceptionResolver> afterHandlerExceptionResolvers = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        if (
            RequestParameterResolver.class.isAssignableFrom((Class<?>) element)
        ) {
            this.requestParameterResolvers.add(
                    app.make(
                        (Class<? extends RequestParameterResolver>) element
                    )
                );
        }
        if (
            ResponseReturnValueResolver.class.isAssignableFrom(
                    (Class<?>) element
                )
        ) {
            this.responseReturnValueResolvers.add(
                    app.make(
                        (Class<? extends ResponseReturnValueResolver>) element
                    )
                );
        }
        if (
            RequestParametersPostResolver.class.isAssignableFrom(
                    (Class<?>) element
                )
        ) {
            this.requestParametersPostResolvers.add(
                    app.make(
                        (Class<? extends RequestParametersPostResolver>) element
                    )
                );
        }
        if (
            HandlerExceptionResolver.class.isAssignableFrom((Class<?>) element)
        ) {
            this.handlerExceptionResolvers.add(
                    app.make(
                        (Class<? extends HandlerExceptionResolver>) element
                    )
                );
        }
        if (
            AfterHandlerExceptionResolver.class.isAssignableFrom(
                    (Class<?>) element
                )
        ) {
            this.afterHandlerExceptionResolvers.add(
                    app.make(
                        (Class<? extends AfterHandlerExceptionResolver>) element
                    )
                );
        }
    }

    public List<RequestParameterResolver> getRequestParameterResolvers() {
        return requestParameterResolvers;
    }

    public List<ResponseReturnValueResolver> getResponseReturnValueResolvers() {
        return responseReturnValueResolvers;
    }

    public List<RequestParametersPostResolver> getRequestParametersPostResolvers() {
        return requestParametersPostResolvers;
    }

    public List<HandlerExceptionResolver> getHandlerExceptionResolvers() {
        return handlerExceptionResolvers;
    }

    public List<AfterHandlerExceptionResolver> getAfterHandlerExceptionResolvers() {
        return afterHandlerExceptionResolvers;
    }
}
