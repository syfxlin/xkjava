/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Lazy;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.Wrapper;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    private final List<String> makeList = new ArrayList<>();

    public BeanAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        // List<Class<? extends Annotation>> beanAnnotations = config()
        //     .get("app.bean_annotations", List.class);
        List<Class<? extends Annotation>> beanAnnotations = Arrays.asList(
            Bean.class
        );
        // bind
        for (Class<? extends Annotation> annotation : beanAnnotations) {
            for (Class<?> _class : this.getTypesAnnotated(annotation)) {
                this.processAnnotation(annotation, _class);
            }
            for (Method method : this.getMethodsAnnotated(annotation)) {
                this.processAnnotation(annotation, method);
            }
        }
        // make
        for (String name : this.makeList) {
            this.app.make(name);
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Method method
    ) {
        ScopeType scopeType = this.getScoopType(method);
        String name = method.getName();
        Class<?> _class = method.getReturnType();
        Annotation anno = AnnotationUtils.getAnnotation(method, annotation);
        if (anno == null) {
            return;
        }
        Bean.BindType bindType = ((Bean) anno).bindType();
        Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod((Bean) anno, _class);
        Wrapper wrapper = (container, with) ->
            method.invoke(container.make(method.getDeclaringClass()));
        this.setInitAndDestroyMethod(
                this.app.bind(
                        name,
                        wrapper,
                        // Method 的 Bean 默认是不绑定 Type 的
                        (bindType == Bean.BindType.BIND)
                            ? _class.getName()
                            : null,
                        scopeType
                    ),
                initAndDestroyMethod
            );
        Object names = ((Bean) anno).name();
        for (String n : (String[]) names) {
            this.setInitAndDestroyMethod(
                    this.app.bind(n, wrapper, null, scopeType),
                    initAndDestroyMethod
                );
        }
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(_class, Lazy.class)
        ) {
            makeList.add(name);
        }
    }

    private void processAnnotation(
        Class<? extends Annotation> annotation,
        Class<?> _class
    ) {
        ScopeType scopeType = this.getScoopType(_class);
        Annotation anno = AnnotationUtils.getAnnotation(_class, annotation);
        if (anno == null) {
            return;
        }
        Bean.BindType bindType = ((Bean) anno).bindType();
        Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod((Bean) anno, _class);
        // Class 的 Bean 默认绑定 Type
        if (
            bindType == Bean.BindType.NO_SET || bindType == Bean.BindType.BIND
        ) {
            this.setInitAndDestroyMethod(
                    this.app.bind(_class, _class, null, scopeType),
                    initAndDestroyMethod
                );
        }
        Object names = ((Bean) anno).name();
        for (String name : (String[]) names) {
            this.setInitAndDestroyMethod(
                    this.app.bind(name, _class.getName(), null, scopeType),
                    initAndDestroyMethod
                );
        }
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(_class, Lazy.class)
        ) {
            makeList.add(_class.getName());
        }
    }

    private ScopeType getScoopType(AnnotatedElement element) {
        ScopeType scopeType = AnnotationUtils.getAnnotationValue(
            element,
            Scope.class,
            "value"
        );
        return scopeType == null ? ScopeType.SINGLETON : scopeType;
    }

    private Method[] getInitAndDestroyMethod(Bean annotation, Class<?> _class) {
        String initMethodName = annotation.initMethod();
        String destroyMethodName = annotation.destroyMethod();
        Method initMethod = StrUtil.isEmpty(initMethodName)
            ? null
            : ReflectUtil.getMethod(_class, initMethodName);
        Method destroyMethod = StrUtil.isEmpty(destroyMethodName)
            ? null
            : ReflectUtil.getMethod(_class, destroyMethodName);
        return new Method[] { initMethod, destroyMethod };
    }

    private void setInitAndDestroyMethod(Binding binding, Method[] methods) {
        if (binding.getInitMethod() == null) {
            binding.setInitMethod(methods[0]);
        }
        if (binding.getDestroyMethod() == null) {
            binding.setDestroyMethod(methods[1]);
        }
    }
}
