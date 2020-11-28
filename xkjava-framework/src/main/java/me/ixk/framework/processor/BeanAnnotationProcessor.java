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
import me.ixk.framework.annotations.AfterRegistry;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Bean.BindType;
import me.ixk.framework.annotations.BeforeRegistry;
import me.ixk.framework.annotations.BindRegistry;
import me.ixk.framework.annotations.Lazy;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Wrapper;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registry.BeanBindRegistry;
import me.ixk.framework.registry.after.AfterBeanRegistry;
import me.ixk.framework.registry.before.BeforeBeanRegistry;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

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
    public void process() {
        // Before
        this.processAnnotation(
                BeforeRegistry.class,
                this::invokeBeforeRegistry,
                this::invokeBeforeRegistry
            );
        // Bind bean
        this.processAnnotation(
                Bean.class,
                this::processAnnotation,
                this::processAnnotation
            );
        // After
        this.processAnnotation(
                AfterRegistry.class,
                this::invokeAfterRegistry,
                this::invokeAfterRegistry
            );
        // Make bean
        for (String beanName : this.makeList) {
            this.app.make(beanName);
        }
    }

    private void processAnnotation(final Method method) {
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            method
        );
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
        final boolean overwrite = beanAnnotation.overwrite();
        final Binding binding =
            this.app.bind(
                    name,
                    wrapper,
                    (bindType == BindType.BIND) ? clazz.getName() : null,
                    scopeType,
                    overwrite
                );
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        for (final String n : beanAnnotation.name()) {
            this.app.alias(n, name, overwrite);
        }
        for (Class<?> type : beanAnnotation.type()) {
            this.app.alias(type.getName(), name, overwrite);
        }
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
        final ScopeType scopeType = this.getScoopType(annotation);
        final BindType bindType = beanAnnotation.bindType();
        final Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        final boolean overwrite = beanAnnotation.overwrite();
        final Binding binding =
            this.invokeRegistry(annotation, clazz, scopeType, overwrite);
        if (bindType == BindType.BIND) {
            this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        }
        for (final String name : beanAnnotation.name()) {
            this.app.alias(name, clazz, overwrite);
        }
        for (Class<?> type : beanAnnotation.type()) {
            this.app.alias(type.getName(), clazz, overwrite);
        }
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

    private void invokeBeforeRegistry(final AnnotatedElement element) {
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            element
        );
        // @BeforeRegistry
        if (annotation.hasAnnotation(BeforeRegistry.class)) {
            for (BeforeRegistry beforeRegistry : annotation.getAnnotations(
                BeforeRegistry.class
            )) {
                for (Class<? extends BeforeBeanRegistry> registry : beforeRegistry.value()) {
                    this.app.make(registry)
                        .register(this.app, element, annotation);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Binding invokeRegistry(
        final MergedAnnotation annotation,
        final Class<?> clazz,
        final ScopeType scopeType,
        final boolean overwrite
    ) {
        // @BindRegistry
        if (annotation.notAnnotation(BindRegistry.class)) {
            return this.app.bind(clazz, clazz, null, scopeType, overwrite);
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
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            element
        );
        // @AfterRegistry
        if (annotation.hasAnnotation(AfterRegistry.class)) {
            for (AfterRegistry afterRegistry : annotation.getAnnotations(
                AfterRegistry.class
            )) {
                for (Class<? extends AfterBeanRegistry> registry : afterRegistry.value()) {
                    this.app.make(registry)
                        .register(this.app, element, annotation);
                }
            }
        }
    }
}
