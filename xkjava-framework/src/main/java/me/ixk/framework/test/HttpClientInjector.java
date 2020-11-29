/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpBase;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.ioc.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.DataBinder;
import me.ixk.framework.ioc.InstanceContext;
import me.ixk.framework.ioc.InstanceInjector;
import me.ixk.framework.ioc.ParameterContext;
import me.ixk.framework.ioc.ParameterContext.ParameterEntry;
import me.ixk.framework.ioc.ParameterInjector;
import me.ixk.framework.test.ClientResponse.RequestProcessor;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ResourceUtils;

/**
 * 请求注入器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 3:52
 */
public class HttpClientInjector implements ParameterInjector, InstanceInjector {

    private Object executeRequest(
        Container container,
        boolean execute,
        final MergedAnnotation annotation
    ) {
        final ClientResponse clientResponse = annotation.getAnnotation(
            ClientResponse.class
        );
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
                try {
                    request.form(
                        kv[0].trim(),
                        value.startsWith(":")
                            ? ResourceUtils.getFile(value)
                            : value
                    );
                } catch (FileNotFoundException e) {
                    throw new ContainerException(e);
                }
            }
        }
        if (clientResponse.contentType() != MimeType.NONE) {
            request.contentType(clientResponse.contentType().asString());
        }
        if (clientResponse.processor() != RequestProcessor.class) {
            container
                .make(clientResponse.processor())
                .process(request, annotation);
        }
        if (!execute) {
            return request;
        }
        return request.execute(clientResponse.async());
    }

    @Override
    public boolean supportsInstance(InstanceContext context, Object instance) {
        return context.getFieldEntries().length > 0;
    }

    @Override
    public Object inject(
        Container container,
        Object instance,
        InstanceContext context,
        DataBinder dataBinder
    ) {
        for (final ChangeableEntry<Field> entry : context.getFieldEntries()) {
            if (entry.isChanged()) {
                continue;
            }
            final Field field = entry.getElement();
            final MergedAnnotation annotation = entry.getAnnotation();
            if (annotation.hasAnnotation(ClientResponse.class)) {
                final Class<?> fieldType = field.getType();
                if (!HttpBase.class.isAssignableFrom(fieldType)) {
                    continue;
                }
                final PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(
                    context.getInstanceType(),
                    field.getName()
                );
                final java.lang.reflect.Method writeMethod = propertyDescriptor ==
                    null
                    ? null
                    : propertyDescriptor.getWriteMethod();
                Object value =
                    this.executeRequest(
                            container,
                            fieldType != HttpRequest.class,
                            annotation
                        );
                if (writeMethod != null) {
                    ReflectUtil.invoke(instance, writeMethod, value);
                } else {
                    ReflectUtil.setFieldValue(instance, field, value);
                }
                entry.setChanged(true);
            }
        }
        return instance;
    }

    @Override
    public boolean supportsParameter(
        ParameterContext context,
        Object[] dependencies
    ) {
        return context.getParameterEntries().length > 0;
    }

    @Override
    public Object[] inject(
        Container container,
        Object[] dependencies,
        ParameterContext context,
        DataBinder dataBinder
    ) {
        final ParameterEntry[] entries = context.getParameterEntries();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isChanged()) {
                continue;
            }
            final MergedAnnotation annotation = entries[i].getAnnotation();
            if (annotation.hasAnnotation(ClientResponse.class)) {
                final Class<?> parameterType =
                    entries[i].getElement().getType();
                if (!HttpBase.class.isAssignableFrom(parameterType)) {
                    continue;
                }
                dependencies[i] =
                    this.executeRequest(
                            container,
                            parameterType != HttpRequest.class,
                            annotation
                        );
                entries[i].setChanged(true);
            }
        }
        return dependencies;
    }
}
