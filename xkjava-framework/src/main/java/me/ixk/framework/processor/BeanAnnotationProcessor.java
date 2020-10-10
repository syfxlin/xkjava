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
import me.ixk.framework.utils.MergeAnnotation;

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
        this.invokeBeforeImport(method);
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
        final BindType bindType = beanAnnotation.get(
            "bindType",
            BindType.class
        );
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
        final Object names = beanAnnotation.get("name");
        for (final String n : (String[]) names) {
            if (name.equals(n)) {
                continue;
            }
            this.app.alias(n, name, scopeType);
        }
        this.invokeAfterImport(method, binding);
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(clazz, Lazy.class)
        ) {
            makeList.add(name);
        }
    }

    private void processAnnotation(final Class<?> clazz) {
        final MergeAnnotation beanAnnotation = AnnotationUtils.getAnnotation(
            clazz,
            Bean.class
        );
        if (beanAnnotation == null) {
            return;
        }
        this.invokeBeforeImport(clazz);
        final ScopeType scopeType = this.getScoopType(clazz);
        final BindType bindType = beanAnnotation.get("bindType");
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        Binding binding = this.invokeImport(clazz, scopeType);
        if (bindType == BindType.BIND) {
            this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        }
        final Object names = beanAnnotation.get("name");
        for (final String name : (String[]) names) {
            this.app.alias(name, clazz, scopeType);
        }
        this.invokeAfterImport(clazz, binding);
        // Add singleton to make list
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(clazz, Lazy.class)
        ) {
            makeList.add(clazz.getName());
        }
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

    @SuppressWarnings("unchecked")
    private void invokeBeforeImport(AnnotatedElement element) {
        final MergeAnnotation beforeAnnotation = AnnotationUtils.getAnnotation(
            element,
            BeforeImport.class
        );
        // @BeforeImport
        if (beforeAnnotation != null) {
            for (Class<BeforeImportBeanRegistrar> registrar : (Class<BeforeImportBeanRegistrar>[]) beforeAnnotation.get(
                BeforeImport.class,
                "value"
            )) {
                this.app.make(registrar)
                    .before(this.app, element, beforeAnnotation);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Binding invokeImport(Class<?> clazz, ScopeType scopeType) {
        final MergeAnnotation importAnnotation = AnnotationUtils.getAnnotation(
            clazz,
            Import.class
        );
        // @Import
        if (importAnnotation == null) {
            return this.app.bind(clazz, clazz, null, scopeType);
        } else {
            return this.app.make(
                    (Class<ImportBeanRegistrar>) importAnnotation.get(
                        Import.class,
                        "value"
                    )
                )
                .register(this.app, clazz, scopeType, importAnnotation);
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeAfterImport(AnnotatedElement element, Binding binding) {
        final MergeAnnotation afterAnnotation = AnnotationUtils.getAnnotation(
            element,
            AfterImport.class
        );
        // @AfterImport
        if (afterAnnotation != null) {
            for (Class<AfterImportBeanRegistrar> registrar : (Class<AfterImportBeanRegistrar>[]) afterAnnotation.get(
                BeforeImport.class,
                "value"
            )) {
                this.app.make(registrar)
                    .after(this.app, element, afterAnnotation, binding);
            }
        }
    }
}
