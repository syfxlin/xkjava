package me.ixk.framework.annotations.processor;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.ReflectionsUtils;
import org.reflections.Reflections;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected final Application app;

    protected final Reflections reflections;

    public AbstractAnnotationProcessor(Application app) {
        this.app = app;
        this.reflections = ReflectionsUtils.make();
    }
}
