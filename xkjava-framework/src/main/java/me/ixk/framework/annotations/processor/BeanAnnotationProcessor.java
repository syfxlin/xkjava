/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Import;
import me.ixk.framework.annotations.Lazy;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Wrapper;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergeAnnotation;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    private final List<String> makeList = new ArrayList<>();

    public BeanAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        Set<Class<?>> classes = this.getTypesAnnotated(Bean.class);
        final Iterator<Class<?>> classIterator = classes.iterator();
        while (classIterator.hasNext()) {
            final Class<?> next = classIterator.next();
            if (classes.contains(next)) {
                this.processAnnotation(next);
                classes = AnnotationUtils.filterConditionAnnotation(classes);
            }
        }
        Set<Method> methods = this.getMethodsAnnotated(Bean.class);
        final Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            final Method next = methodIterator.next();
            if (methods.contains(next)) {
                this.processAnnotation(next);
                methods = AnnotationUtils.filterConditionAnnotation(methods);
            }
        }
        this.app.setAttribute("makeSingletonBeanList", this.makeList);
    }

    private void processAnnotation(final Method method) {
        final ScopeType scopeType = this.getScoopType(method);
        final String name = method.getName();
        final Class<?> clazz = method.getReturnType();
        final MergeAnnotation beanAnnotation = AnnotationUtils.getAnnotation(
            method,
            Bean.class
        );
        if (beanAnnotation == null) {
            return;
        }
        final Bean.BindType bindType = beanAnnotation.get("bindType");
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final Wrapper wrapper = (container, with) -> container.call(method);
        final Binding binding =
            this.app.bind(
                    name,
                    wrapper,
                    (bindType == Bean.BindType.BIND) ? clazz.getName() : null,
                    scopeType
                );
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        final Object names = beanAnnotation.get("name");
        for (final String n : (String[]) names) {
            if (name.equals(n)) {
                continue;
            }
            this.app.alias(n, name, binding.getScope());
        }
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(clazz, Lazy.class)
        ) {
            makeList.add(name);
        }
    }

    private void processAnnotation(final Class<?> clazz) {
        final ScopeType scopeType = this.getScoopType(clazz);
        final MergeAnnotation anno = AnnotationUtils.getAnnotation(
            clazz,
            Bean.class
        );
        if (anno == null) {
            return;
        }
        final Bean.BindType bindType = anno.get("bindType");
        doBind(clazz, scopeType, anno, bindType);
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(clazz, Lazy.class)
        ) {
            makeList.add(clazz.getName());
        }
    }

    private ScopeType getScoopType(final AnnotatedElement element) {
        final ScopeType scopeType = AnnotationUtils.getAnnotationValue(
            element,
            Scope.class,
            "type"
        );
        return scopeType == null ? ScopeType.SINGLETON : scopeType;
    }

    private Method[] getInitAndDestroyMethod(
        final MergeAnnotation annotation,
        final Class<?> clazz
    ) {
        final String initMethodName = annotation.get("initMethod");
        final String destroyMethodName = annotation.get("destroyMethod");
        final Method initMethod = StrUtil.isEmpty(initMethodName)
            ? null
            : ReflectUtil.getMethod(clazz, initMethodName);
        final Method destroyMethod = StrUtil.isEmpty(destroyMethodName)
            ? null
            : ReflectUtil.getMethod(clazz, destroyMethodName);
        return new Method[] { initMethod, destroyMethod };
    }

    private void setInitAndDestroyMethod(
        final Binding binding,
        final Method[] methods
    ) {
        if (binding.getInitMethod() == null) {
            binding.setInitMethod(methods[0]);
        }
        if (binding.getDestroyMethod() == null) {
            binding.setDestroyMethod(methods[1]);
        }
    }

    private void doBind(
        Class<?> clazz,
        ScopeType scopeType,
        MergeAnnotation beanAnnotation,
        Bean.BindType bindType
    ) {
        MergeAnnotation annotation = AnnotationUtils.getAnnotation(
            clazz,
            Import.class
        );
        if (annotation == null) {
            final Method[] initAndDestroyMethod =
                this.getInitAndDestroyMethod(beanAnnotation, clazz);
            final Binding binding =
                this.app.bind(clazz, clazz, null, scopeType);
            if (bindType == Bean.BindType.BIND) {
                this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
            }
            final Object names = beanAnnotation.get("name");
            for (final String name : (String[]) names) {
                this.app.alias(name, clazz, binding.getScope());
            }
            return;
        }
        Class<?> registrarType = annotation.get(Import.class, "value");
        ReflectUtil.invoke(
            this.app.make(registrarType),
            "register",
            this.app,
            clazz,
            scopeType,
            annotation
        );
    }
}
