package me.ixk.framework.annotations.processor;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.ReflectionsUtils;
import org.reflections.Reflections;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected Application app;

    protected Reflections reflections;

    public AbstractAnnotationProcessor(Application app) {
        this.app = app;
        this.reflections = ReflectionsUtils.make();
    }
}
