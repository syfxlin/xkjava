/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.processor;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import me.ixk.framework.annotation.core.AfterRegistry;
import me.ixk.framework.annotation.core.AnnotationProcessor;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.BeforeRegistry;
import me.ixk.framework.annotation.core.BindRegistry;
import me.ixk.framework.annotation.core.Import;
import me.ixk.framework.annotation.core.Lazy;
import me.ixk.framework.ioc.ImportSelector;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.entity.AnnotatedEntry;
import me.ixk.framework.ioc.entity.Binding;
import me.ixk.framework.ioc.factory.FactoryBean;
import me.ixk.framework.registry.BeanBindRegistry;
import me.ixk.framework.registry.after.AfterBeanRegistry;
import me.ixk.framework.registry.before.BeforeBeanRegistry;
import me.ixk.framework.util.MergedAnnotation;

/**
 * Bean 注解处理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:49
 */
@AnnotationProcessor
public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {

    private final List<String> makeList = new ArrayList<>();

    public BeanAnnotationProcessor(final XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        // Before
        this.processAnnotation(
                new Class[] { BeforeRegistry.class, Import.class },
                clazz -> {
                    this.invokeBeforeRegistry(clazz);
                    if (MergedAnnotation.has(clazz, Import.class)) {
                        this.processImport(clazz, this::invokeBeforeRegistry);
                    }
                },
                this::invokeBeforeRegistry
            );
        // Bind bean
        this.processAnnotation(
                new Class[] { Bean.class, Import.class },
                clazz -> {
                    this.invokeBinding(clazz);
                    if (MergedAnnotation.has(clazz, Import.class)) {
                        this.processImport(clazz, this::invokeBinding);
                    }
                },
                this::invokeBinding
            );
        // After
        this.processAnnotation(
                new Class[] { AfterRegistry.class, Import.class },
                clazz -> {
                    this.invokeAfterRegistry(clazz);
                    if (MergedAnnotation.has(clazz, Import.class)) {
                        this.processImport(clazz, this::invokeAfterRegistry);
                    }
                },
                this::invokeAfterRegistry
            );
        // Make bean
        for (final String beanName : this.makeList) {
            this.app.make(beanName, Object.class);
        }
    }

    private void invokeBinding(final Method method) {
        final MergedAnnotation annotation = MergedAnnotation.from(method);
        final String scopeType = this.getScoopType(annotation);
        final String name = method.getName();
        final Class<?> clazz = method.getReturnType();
        final Bean beanAnnotation = annotation.getAnnotation(Bean.class);
        if (beanAnnotation == null) {
            return;
        }
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final Binding binding =
            this.invokeRegistry(annotation, method, scopeType);
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        for (final String n : beanAnnotation.name()) {
            this.app.setAlias(n, binding.getName());
        }
        if (
            ScopeType.SINGLETON.equalsIgnoreCase(scopeType) &&
            annotation.notAnnotation(Lazy.class)
        ) {
            makeList.add(name);
        }
    }

    private void invokeBinding(final Class<?> clazz) {
        final MergedAnnotation annotation = MergedAnnotation.from(clazz);
        final Bean beanAnnotation = annotation.getAnnotation(Bean.class);
        if (beanAnnotation == null) {
            return;
        }
        final String scopeType = this.getScoopType(annotation);
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final Binding binding =
            this.invokeRegistry(annotation, clazz, scopeType);
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        for (final String name : beanAnnotation.name()) {
            this.app.setAlias(name, binding.getName());
        }
        // Add singleton to make list
        if (
            ScopeType.SINGLETON.equalsIgnoreCase(scopeType) &&
            annotation.notAnnotation(Lazy.class)
        ) {
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
        if (methods[0] != null) {
            binding.getInitMethods().add(methods[0]);
        }
        if (methods[1] != null) {
            binding.getDestroyMethods().add(methods[1]);
        }
    }

    private void invokeBeforeRegistry(final AnnotatedElement element) {
        final MergedAnnotation annotation = MergedAnnotation.from(element);
        // @BeforeRegistry
        if (annotation.hasAnnotation(BeforeRegistry.class)) {
            for (final BeforeRegistry beforeRegistry : annotation.getAnnotations(
                BeforeRegistry.class
            )) {
                for (final Class<? extends BeforeBeanRegistry> registry : beforeRegistry.value()) {
                    this.app.make(registry)
                        .register(this.app, element, annotation);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Binding invokeRegistry(
        final MergedAnnotation annotation,
        final Method method,
        final String scopeType
    ) {
        if (annotation.notAnnotation(BindRegistry.class)) {
            return this.app.bind(
                    method.getName(),
                    new FactoryBean<>() {
                        @Override
                        public Object getObject() {
                            return app.call(method);
                        }

                        @Override
                        public Class<?> getObjectType() {
                            return method.getReturnType();
                        }
                    },
                    scopeType
                );
        } else {
            return this.app.make(
                    (Class<BeanBindRegistry>) annotation.get(
                        BindRegistry.class,
                        "value"
                    )
                )
                .register(this.app, method, scopeType, annotation);
        }
    }

    @SuppressWarnings("unchecked")
    private Binding invokeRegistry(
        final MergedAnnotation annotation,
        final Class<?> clazz,
        final String scopeType
    ) {
        // @BindRegistry
        if (annotation.notAnnotation(BindRegistry.class)) {
            return this.app.bind(clazz, scopeType);
        } else {
            return this.app.make(
                    (Class<BeanBindRegistry>) annotation.get(
                        BindRegistry.class,
                        "value"
                    )
                )
                .register(this.app, clazz, scopeType, annotation);
        }
    }

    private void invokeAfterRegistry(final AnnotatedElement element) {
        final MergedAnnotation annotation = MergedAnnotation.from(element);
        // @AfterRegistry
        if (annotation.hasAnnotation(AfterRegistry.class)) {
            for (final AfterRegistry afterRegistry : annotation.getAnnotations(
                AfterRegistry.class
            )) {
                for (final Class<? extends AfterBeanRegistry> registry : afterRegistry.value()) {
                    this.app.make(registry)
                        .register(this.app, element, annotation);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processImport(
        final Class<?> clazz,
        final Consumer<Class<?>> consumer
    ) {
        final MergedAnnotation annotation = MergedAnnotation.from(clazz);
        final Import importAnnotation = annotation.getAnnotation(Import.class);
        final AnnotatedEntry<Class<?>> annotatedEntry = new AnnotatedEntry<>(
            clazz,
            annotation
        );
        for (final Class<?> importClass : importAnnotation.value()) {
            if (ImportSelector.class.isAssignableFrom(importClass)) {
                final String[] selectImports =
                    this.app.make((Class<? extends ImportSelector>) importClass)
                        .selectImports(annotatedEntry);
                for (final String selectImport : selectImports) {
                    consumer.accept(ClassUtil.loadClass(selectImport));
                }
            } else {
                consumer.accept(importClass);
            }
        }
    }
}
