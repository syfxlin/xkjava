/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.util.ReflectUtil;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;

public class AnnotationProcessorManager {
    protected final XkJava app;

    protected final Map<String, AnnotationProcessor> processors;

    public AnnotationProcessorManager(XkJava app) {
        this.app = app;
        this.processors = new ConcurrentHashMap<>();
    }

    public AnnotationProcessor getProcessor(String name) {
        return this.processors.get(name);
    }

    public AnnotationProcessor getProcessor(AnnotationProcessor processor) {
        return processor;
    }

    public boolean hasProcessor(String name) {
        return this.processors.containsKey(name);
    }

    public void setProcessor(String name, AnnotationProcessor processor) {
        this.processors.put(name, processor);
    }

    protected AnnotationProcessor getProcessorInstance(String name) {
        try {
            AnnotationProcessor processor = (AnnotationProcessor) ReflectUtil.newInstance(
                Class.forName(name),
                this.app
            );
            this.setProcessor(name, processor);
            return processor;
        } catch (ClassNotFoundException e) {
            throw new AnnotationProcessorException(
                "Instantiating annotation processor failed",
                e
            );
        }
    }

    public AnnotationProcessor register(String processor) {
        return this.register(processor, false);
    }

    public AnnotationProcessor register(String processor, boolean force) {
        AnnotationProcessor result;
        if (!force && (result = this.getProcessor(processor)) != null) {
            return result;
        }
        result = this.getProcessorInstance(processor);
        return result;
    }

    public AnnotationProcessor register(Class<?> clazz) {
        return this.register(clazz.getName());
    }

    public AnnotationProcessor register(Class<?> clazz, boolean force) {
        return this.register(clazz.getName(), force);
    }

    public Set<AnnotationProcessor> registers(Set<Class<?>> processors) {
        return processors
            .stream()
            .map(this::register)
            .collect(Collectors.toSet());
    }

    public void process() {
        this.processors.values().forEach(AnnotationProcessor::process);
    }
}
