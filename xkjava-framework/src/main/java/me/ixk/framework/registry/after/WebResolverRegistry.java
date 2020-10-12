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
import me.ixk.framework.web.RequestParameterResolver;
import me.ixk.framework.web.RequestParametersPostResolver;
import me.ixk.framework.web.ResponseReturnValueResolver;

@Component(name = "webHandlerRegistry")
public class WebResolverRegistry implements AfterImportBeanRegistry {

    private final List<RequestParameterResolver> requestParameterResolvers = new ArrayList<>();
    private final List<ResponseReturnValueResolver> responseReturnValueResolvers = new ArrayList<>();
    private final List<RequestParametersPostResolver> requestParametersPostResolvers = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void after(final XkJava app, final AnnotatedElement element,
        final MergedAnnotation annotation) {
        if (RequestParameterResolver.class
            .isAssignableFrom((Class<?>) element)) {
            this.requestParameterResolvers.add(
                app.make((Class<? extends RequestParameterResolver>) element));
        }
        if (ResponseReturnValueResolver.class
            .isAssignableFrom((Class<?>) element)) {
            this.responseReturnValueResolvers.add(app.make(
                (Class<? extends ResponseReturnValueResolver>) element));
        }
        if (RequestParametersPostResolver.class
            .isAssignableFrom((Class<?>) element)) {
            this.requestParametersPostResolvers.add(app.make(
                (Class<? extends RequestParametersPostResolver>) element));
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

}
