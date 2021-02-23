/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.framework.exception.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.processor.AnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注解处理管理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:11
 */
public class AnnotationProcessorManager {

    private static final Logger log = LoggerFactory.getLogger(
        AnnotationProcessorManager.class
    );

    protected final XkJava app;

    protected final Map<String, AnnotationProcessor> processors;

    public AnnotationProcessorManager(final XkJava app) {
        this.app = app;
        this.processors = new LinkedHashMap<>();
    }

    public AnnotationProcessor getProcessor(final String name) {
        return this.processors.get(name);
    }

    public AnnotationProcessor getProcessor(
        final AnnotationProcessor processor
    ) {
        return processor;
    }

    public boolean hasProcessor(final String name) {
        return this.processors.containsKey(name);
    }

    public void setProcessor(
        final String name,
        final AnnotationProcessor processor
    ) {
        this.processors.put(name, processor);
    }

    protected AnnotationProcessor getProcessorInstance(final String name) {
        try {
            final Class<AnnotationProcessor> processorClass = ClassUtil.loadClass(
                name
            );
            this.app.bind(processorClass);
            final AnnotationProcessor processor = this.app.make(processorClass);
            this.setProcessor(name, processor);
            return processor;
        } catch (final UtilException e) {
            log.error("Instantiating annotation processor failed");
            throw new AnnotationProcessorException(
                "Instantiating annotation processor failed",
                e
            );
        }
    }

    public AnnotationProcessor register(final String processor) {
        return this.register(processor, false);
    }

    public AnnotationProcessor register(
        final String processor,
        final boolean force
    ) {
        AnnotationProcessor result;
        if (!force && (result = this.getProcessor(processor)) != null) {
            return result;
        }
        result = this.getProcessorInstance(processor);
        return result;
    }

    public AnnotationProcessor register(final Class<?> clazz) {
        return this.register(clazz.getName());
    }

    public AnnotationProcessor register(
        final Class<?> clazz,
        final boolean force
    ) {
        return this.register(clazz.getName(), force);
    }

    public Set<AnnotationProcessor> registers(final Set<Class<?>> processors) {
        return processors
            .stream()
            .map(this::register)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void process() {
        this.processors.values().forEach(AnnotationProcessor::process);
    }
}
