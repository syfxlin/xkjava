package me.ixk.framework.kernel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.ioc.Application;

public class AnnotationProcessorManager {
    protected Application app;

    protected Map<String, AnnotationProcessor> processors;

    public AnnotationProcessorManager(Application app) {
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
            AnnotationProcessor processor = (AnnotationProcessor) Class
                .forName(name)
                .getConstructor(Application.class)
                .newInstance(this.app);
            this.setProcessor(name, processor);
            return processor;
        } catch (
            InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException
            | ClassNotFoundException e
        ) {
            e.printStackTrace();
        }
        return null;
    }

    public AnnotationProcessor register(String processor) {
        return this.register(processor, false);
    }

    public AnnotationProcessor register(String processor, boolean force) {
        AnnotationProcessor result = null;
        if (!force && (result = this.getProcessor(processor)) != null) {
            return result;
        }
        result = this.getProcessorInstance(processor);
        return result;
    }

    public AnnotationProcessor register(Class<?> _class) {
        return this.register(_class.getName());
    }

    public AnnotationProcessor register(Class<?> _class, boolean force) {
        return this.register(_class.getName(), force);
    }

    public List<AnnotationProcessor> registers(List<Class<?>> processors) {
        return processors
            .stream()
            .map(this::register)
            .collect(Collectors.toList());
    }

    public void process() {
        this.processors.values().forEach(AnnotationProcessor::process);
    }
}
