/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.Component;

@Component(name = "webHandlerRegistry")
public class WebHandlerRegistry {
    private final List<RequestParameterResolver> requestParameterResolvers = new ArrayList<>();
    private final List<ResponseReturnValueResolver> responseReturnValueResolvers = new ArrayList<>();
    private final List<RequestParametersPostResolver> requestParametersPostResolvers = new ArrayList<>();

    public WebHandlerRegistry() {
        this.requestParameterResolvers.add(new AnnotatedParameterResolver());
        this.requestParameterResolvers.add(
                new WebDataBinderParameterResolver()
            );
        this.requestParametersPostResolvers.add(
                new ValidationParametersResolver()
            );
    }

    public void addRequestParameterResolver(RequestParameterResolver resolver) {
        this.requestParameterResolvers.add(resolver);
    }

    public void addResponseReturnValueResolver(
        ResponseReturnValueResolver resolver
    ) {
        this.responseReturnValueResolvers.add(resolver);
    }

    public void addRequestParametersPostResolver(
        RequestParametersPostResolver resolver
    ) {
        this.requestParametersPostResolvers.add(resolver);
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
