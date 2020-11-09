/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.test.ClientResponse.RequestProcessor;
import me.ixk.framework.utils.AnnotationUtils;

/**
 * 请求注入器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 3:52
 */
public class HttpClientInjector implements ParameterInjector, InstanceInjector {

    @Override
    public Object inject(
        final Container container,
        final Binding binding,
        final Object instance,
        final Class<?> instanceClass,
        final DataBinder dataBinder
    ) {
        for (Field field : instanceClass.getDeclaredFields()) {
            final ClientResponse clientResponse = AnnotationUtils.getAnnotation(
                field,
                ClientResponse.class
            );
            if (clientResponse != null) {
                final PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                    instanceClass,
                    field.getName()
                );
                final java.lang.reflect.Method writeMethod = propertyDescriptor ==
                    null
                    ? null
                    : propertyDescriptor.getWriteMethod();
                Object value = this.executeRequest(container, clientResponse);
                if (writeMethod != null) {
                    ReflectUtil.invoke(instance, writeMethod, value);
                } else {
                    ReflectUtil.setFieldValue(instance, field, value);
                }
            }
        }
        return instance;
    }

    @Override
    public Object[] inject(
        final Container container,
        final Binding binding,
        final Executable method,
        final Parameter[] parameters,
        final String[] parameterNames,
        final Object[] dependencies,
        final DataBinder dataBinder
    ) {
        for (int i = 0; i < parameters.length; i++) {
            final ClientResponse clientResponse = AnnotationUtils.getAnnotation(
                parameters[i],
                ClientResponse.class
            );
            if (clientResponse != null) {
                dependencies[i] =
                    this.executeRequest(container, clientResponse);
            }
        }
        return dependencies;
    }

    private HttpResponse executeRequest(
        Container container,
        final ClientResponse clientResponse
    ) {
        final HttpRequest request = HttpRequest
            .get(clientResponse.url())
            .method(Method.valueOf(clientResponse.method().asString()))
            .cookie(String.join(";", clientResponse.cookie()));
        for (String header : clientResponse.header()) {
            final String[] kv = header.split(":");
            request.header(kv[0].trim(), kv[1].trim());
        }
        if (!clientResponse.body().isEmpty()) {
            request.body(clientResponse.body());
        } else if (clientResponse.form().length > 0) {
            for (String form : clientResponse.form()) {
                final String[] kv = form.split("=");
                final String value = kv[1].trim();
                request.form(
                    kv[0].trim(),
                    value.startsWith(":")
                        ? ResourceUtil.getResourceObj(value)
                        : value
                );
            }
        }
        if (clientResponse.contentType() != MimeType.NONE) {
            request.contentType(clientResponse.contentType().asString());
        }
        if (clientResponse.processor() != RequestProcessor.class) {
            container.make(clientResponse.processor()).process(request);
        }
        return request.execute(clientResponse.async());
    }
}
