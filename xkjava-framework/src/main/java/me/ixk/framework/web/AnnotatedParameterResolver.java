/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import static me.ixk.framework.helpers.Util.caseGet;

import com.fasterxml.jackson.databind.node.NullNode;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import me.ixk.framework.annotations.BodyValue;
import me.ixk.framework.annotations.CookieValue;
import me.ixk.framework.annotations.HeaderValue;
import me.ixk.framework.annotations.PartValue;
import me.ixk.framework.annotations.PathValue;
import me.ixk.framework.annotations.QueryValue;
import me.ixk.framework.annotations.RequestValue;
import me.ixk.framework.annotations.SessionValue;
import me.ixk.framework.http.Request;
import me.ixk.framework.utils.MergedAnnotation;

public class AnnotatedParameterResolver implements RequestParameterResolver {

    @Override
    public boolean supportsParameter(
        final Object value,
        final MethodParameter parameter
    ) {
        if (value == null) {
            final MergedAnnotation annotation = parameter.getParameterAnnotation();
            return (
                annotation.hasAnnotation(QueryValue.class) ||
                annotation.hasAnnotation(BodyValue.class) ||
                annotation.hasAnnotation(PathValue.class) ||
                annotation.hasAnnotation(PartValue.class) ||
                annotation.hasAnnotation(HeaderValue.class) ||
                annotation.hasAnnotation(CookieValue.class) ||
                annotation.hasAnnotation(SessionValue.class) ||
                annotation.hasAnnotation(RequestValue.class)
            );
        }
        return false;
    }

    @Override
    public Object resolveParameter(
        final Object value,
        final MethodParameter parameter,
        final WebContext context,
        final WebDataBinder binder
    ) {
        return this.getValue(parameter, context.getRequest());
    }

    private Object getValue(
        final MethodParameter parameter,
        final Request request
    ) {
        MergedAnnotation annotation = parameter.getParameterAnnotation();
        Class<? extends Annotation> annotationType = null;
        Function<String, Object> fun = n -> null;
        if (annotation.hasAnnotation(QueryValue.class)) {
            annotationType = QueryValue.class;
            fun = request::query;
        } else if (annotation.hasAnnotation(BodyValue.class)) {
            annotationType = BodyValue.class;
            fun = request::input;
        } else if (annotation.hasAnnotation(PathValue.class)) {
            annotationType = PathValue.class;
            fun = request::route;
        } else if (annotation.hasAnnotation(PartValue.class)) {
            annotationType = PartValue.class;
            fun = request::file;
        } else if (annotation.hasAnnotation(HeaderValue.class)) {
            annotationType = HeaderValue.class;
            fun = request::header;
        } else if (annotation.hasAnnotation(CookieValue.class)) {
            annotationType = CookieValue.class;
            fun = request::cookie;
        } else if (annotation.hasAnnotation(SessionValue.class)) {
            annotationType = SessionValue.class;
            fun = request::session;
        } else if (annotation.hasAnnotation(RequestValue.class)) {
            annotationType = RequestValue.class;
            fun = request::getAttribute;
        }
        String name = annotation.get(annotationType, "name");
        if (name.isEmpty()) {
            name = parameter.getParameterName();
        }
        Object value = caseGet(name, fun);
        if (
            (value == null || value == NullNode.getInstance()) &&
            (boolean) annotation.get(annotationType, "required")
        ) {
            throw new NullPointerException(
                "Target [" +
                parameter.getControllerClass().getName() +
                "@" +
                parameter.getMethod().getName() +
                "(" +
                parameter.getParameterName() +
                ")] is required, but inject value is null"
            );
        }
        return value;
    }
}
