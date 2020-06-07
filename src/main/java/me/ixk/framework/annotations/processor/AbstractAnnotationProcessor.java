package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationScanner;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected Application app;

    protected Map<Class<? extends Annotation>, Set<Class<?>>> annotations;

    public AbstractAnnotationProcessor(Application app) {
        this.app = app;
        this.annotations = AnnotationScanner.scan(this.app.getScanPackages());
    }
}
