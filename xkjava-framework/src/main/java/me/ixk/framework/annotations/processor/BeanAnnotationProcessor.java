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

    public BeanAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        Set<Class<?>> classes = this.getTypesAnnotated(Bean.class);
        Iterator<Class<?>> classIterator = classes.iterator();
        while (classIterator.hasNext()) {
            Class<?> next = classIterator.next();
            if (classes.contains(next)) {
                this.processAnnotation(next);
                classes = AnnotationUtils.filterConditionAnnotation(classes);
            }
        }
        Set<Method> methods = this.getMethodsAnnotated(Bean.class);
        Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            Method next = methodIterator.next();
            if (methods.contains(next)) {
                this.processAnnotation(next);
                methods = AnnotationUtils.filterConditionAnnotation(methods);
            }
        }
        this.app.setAttribute("makeSingletonBeanList", this.makeList);
    }

    private void processAnnotation(Method method) {
        ScopeType scopeType = this.getScoopType(method);
        String name = method.getName();
        Class<?> clazz = method.getReturnType();
        MergeAnnotation beanAnnotation = AnnotationUtils.getAnnotation(
            method,
            Bean.class
        );
        if (beanAnnotation == null) {
            return;
        }
        Bean.BindType bindType = beanAnnotation.get("bindType");
        Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(beanAnnotation, clazz);
        Wrapper wrapper = (container, with) -> container.call(method);
        Binding binding =
            this.app.bind(
                    name,
                    wrapper,
                    (bindType == Bean.BindType.BIND) ? clazz.getName() : null,
                    scopeType
                );
        this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        Object names = beanAnnotation.get("name");
        for (String n : (String[]) names) {
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

    private void processAnnotation(Class<?> clazz) {
        ScopeType scopeType = this.getScoopType(clazz);
        MergeAnnotation anno = AnnotationUtils.getAnnotation(clazz, Bean.class);
        if (anno == null) {
            return;
        }
        Bean.BindType bindType = anno.get("bindType");
        Method[] initAndDestroyMethod =
            this.getInitAndDestroyMethod(anno, clazz);
        Binding binding = this.app.bind(clazz, clazz, null, scopeType);
        if (bindType == Bean.BindType.BIND) {
            this.setInitAndDestroyMethod(binding, initAndDestroyMethod);
        }
        Object names = anno.get("name");
        for (String name : (String[]) names) {
            this.app.alias(name, clazz, binding.getScope());
        }
        if (
            scopeType.isSingleton() &&
            !AnnotationUtils.hasAnnotation(clazz, Lazy.class)
        ) {
            makeList.add(clazz.getName());
        }
    }

    private ScopeType getScoopType(AnnotatedElement element) {
        ScopeType scopeType = AnnotationUtils.getAnnotationValue(
            element,
            Scope.class,
            "type"
        );
        return scopeType == null ? ScopeType.SINGLETON : scopeType;
    }

    private Method[] getInitAndDestroyMethod(
        MergeAnnotation annotation,
        Class<?> clazz
    ) {
        String initMethodName = annotation.get("initMethod");
        String destroyMethodName = annotation.get("destroyMethod");
        Method initMethod = StrUtil.isEmpty(initMethodName)
            ? null
            : ReflectUtil.getMethod(clazz, initMethodName);
        Method destroyMethod = StrUtil.isEmpty(destroyMethodName)
            ? null
            : ReflectUtil.getMethod(clazz, destroyMethodName);
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
