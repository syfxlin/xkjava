/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.RequestAttribute;
import me.ixk.framework.registrar.RequestAttributeRegistrar;
import me.ixk.framework.utils.MergedAnnotation;

@Component(name = "requestAttributeRegistry")
public class RequestAttributeRegistry {
    private final Map<Method, Map<String, RequestAttributeDefinition>> registrars = new ConcurrentHashMap<>();

    public void addRegistrar(
        final Method method,
        final MergedAnnotation annotation,
        final RequestAttributeRegistrar registrar
    ) {
        final Map<String, RequestAttributeDefinition> definitionMap =
            this.registrars.getOrDefault(method, new ConcurrentHashMap<>());
        for (final RequestAttribute requestAttribute : annotation.getAnnotations(
            RequestAttribute.class
        )) {
            definitionMap.put(
                requestAttribute.name(),
                new RequestAttributeDefinition(method, annotation, registrar)
            );
        }
        this.registrars.put(method, definitionMap);
    }

    public Map<String, RequestAttributeDefinition> getRegistrar(Method method) {
        return this.registrars.get(method);
    }

    public static class RequestAttributeDefinition {
        private final RequestAttributeRegistrar registrar;
        private final MergedAnnotation annotation;
        private final Method method;

        public RequestAttributeDefinition(
            final Method method,
            final MergedAnnotation annotation,
            final RequestAttributeRegistrar registrar
        ) {
            this.method = method;
            this.registrar = registrar;
            this.annotation = annotation;
        }

        public RequestAttributeRegistrar getRegistrar() {
            return registrar;
        }

        public MergedAnnotation getAnnotation() {
            return annotation;
        }

        public Method getMethod() {
            return method;
        }
    }
}
