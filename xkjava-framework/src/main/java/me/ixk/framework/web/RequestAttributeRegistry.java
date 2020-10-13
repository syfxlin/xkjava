/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.RequestAttribute;
import me.ixk.framework.utils.MergedAnnotation;

@Component(name = "requestAttributeRegistry")
public class RequestAttributeRegistry {
    private final Map<Method, Map<String, RequestAttributeDefinition>> registries = new ConcurrentHashMap<>();

    public void addRegistry(
        final Method method,
        final MergedAnnotation annotation,
        final me.ixk.framework.registry.request.RequestAttributeRegistry registry
    ) {
        final Map<String, RequestAttributeDefinition> definitionMap =
            this.registries.getOrDefault(method, new ConcurrentHashMap<>());
        for (final RequestAttribute requestAttribute : annotation.getAnnotations(
            RequestAttribute.class
        )) {
            definitionMap.put(
                requestAttribute.name(),
                new RequestAttributeDefinition(method, annotation, registry)
            );
        }
        this.registries.put(method, definitionMap);
    }

    public Map<String, RequestAttributeDefinition> getRegistry(Method method) {
        return this.registries.get(method);
    }

    public static class RequestAttributeDefinition {
        private final me.ixk.framework.registry.request.RequestAttributeRegistry registry;
        private final MergedAnnotation annotation;
        private final Method method;

        public RequestAttributeDefinition(
            final Method method,
            final MergedAnnotation annotation,
            final me.ixk.framework.registry.request.RequestAttributeRegistry registry
        ) {
            this.method = method;
            this.registry = registry;
            this.annotation = annotation;
        }

        public me.ixk.framework.registry.request.RequestAttributeRegistry getRegistry() {
            return registry;
        }

        public MergedAnnotation getAnnotation() {
            return annotation;
        }

        public Method getMethod() {
            return method;
        }
    }
}