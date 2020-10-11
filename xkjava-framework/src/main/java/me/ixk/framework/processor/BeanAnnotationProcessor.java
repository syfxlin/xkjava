/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.AfterImport;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Bean.BindType;
import me.ixk.framework.annotations.BeforeImport;
import me.ixk.framework.annotations.Import;
import me.ixk.framework.annotations.Lazy;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Wrapper;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registrar.AfterImportBeanRegistrar;
import me.ixk.framework.registrar.BeforeImportBeanRegistrar;
import me.ixk.framework.registrar.ImportBeanRegistrar;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

@AnnotationProcessor
public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    private final List<String> makeList = new ArrayList<>();

    public BeanAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                Bean.class,
                this::processAnnotation,
                this::processAnnotation
            );
        this.app.setAttribute("makeSingletonBeanList", this.makeList);
    }

    private void processAnnotation(final Method method) {
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            method
        );
        this.invokeBeforeImport(annotation, method);
        final ScopeType scopeType = this.getScoopType(annotation);
        final String name = method.getName();
        final Class<?> clazz = method.getReturnType();
        final Bean beanAnnotation = annotation.getAnnotation(Bean.class);
        if (beanAnnotation == null) {
            return;
        }
        final BindType bindType = beanAnnotation.bindType();
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final Wrapper wrapper = (container, with) -> container.call(method);
        final Binding binding =
            this.app.bind(
                    name,
                    wrapper,
                    (bindType == BindType.BIND) ? clazz.getName() : null,
                    scopeType
                );
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        final Object names = beanAnnotation.name();
        for (final String n : (String[]) names) {
            if (name.equals(n)) {
                continue;
            }
            this.app.alias(n, name, scopeType);
        }
        this.invokeAfterImport(annotation, method, binding);
        if (scopeType.isSingleton() && annotation.notAnnotation(Lazy.class)) {
            makeList.add(name);
        }
    }

    private void processAnnotation(final Class<?> clazz) {
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            clazz
        );
        final Bean beanAnnotation = annotation.getAnnotation(Bean.class);
        if (beanAnnotation == null) {
            return;
        }
        this.invokeBeforeImport(annotation, clazz);
        final ScopeType scopeType = this.getScoopType(annotation);
        final BindType bindType = beanAnnotation.bindType();
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final Binding binding = this.invokeImport(annotation, clazz, scopeType);
        if (bindType == BindType.BIND) {
            this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        }
        final Object names = beanAnnotation.name();
        for (final String name : (String[]) names) {
            this.app.alias(name, clazz, scopeType);
        }
        this.invokeAfterImport(annotation, clazz, binding);
        // Add singleton to make list
        if (scopeType.isSingleton() && annotation.notAnnotation(Lazy.class)) {
            makeList.add(clazz.getName());
        }
    }

    private Method[] getInitAndDestroyMethod(
        final Bean annotation,
        final Class<?> clazz
    ) {
        final String initMethodName = annotation.initMethod();
        final String destroyMethodName = annotation.destroyMethod();
        final Method initMethod = StrUtil.isEmpty(initMethodName)
            ? null
            : ReflectUtil.getMethodByName(clazz, initMethodName);
        final Method destroyMethod = StrUtil.isEmpty(destroyMethodName)
            ? null
            : ReflectUtil.getMethodByName(clazz, destroyMethodName);
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

    @SuppressWarnings("unchecked")
    private void invokeBeforeImport(
        final MergedAnnotation annotation,
        final AnnotatedElement element
    ) {
        // @BeforeImport
        if (annotation.hasAnnotation(BeforeImport.class)) {
            for (final Class<BeforeImportBeanRegistrar> registrar : (Class<BeforeImportBeanRegistrar>[]) annotation.get(
                BeforeImport.class,
                "value"
            )) {
                this.app.make(registrar).before(this.app, element, annotation);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Binding invokeImport(
        final MergedAnnotation annotation,
        final Class<?> clazz,
        final ScopeType scopeType
    ) {
        // @Import
        if (annotation.notAnnotation(Import.class)) {
            return this.app.bind(clazz, clazz, null, scopeType);
        } else {
            return this.app.make(
                    (Class<ImportBeanRegistrar>) annotation.get(
                        Import.class,
                        "value"
                    )
                )
                .register(this.app, clazz, scopeType, annotation);
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeAfterImport(
        MergedAnnotation annotation,
        final AnnotatedElement element,
        final Binding binding
    ) {
        // @AfterImport
        if (annotation.hasAnnotation(AfterImport.class)) {
            for (final Class<AfterImportBeanRegistrar> registrar : (Class<AfterImportBeanRegistrar>[]) annotation.get(
                AfterImport.class,
                "value"
            )) {
                this.app.make(registrar)
                    .after(this.app, element, annotation, binding);
            }
        }
    }
}
