package me.ixk.framework.annotations.processor;

import me.ixk.framework.ioc.Application;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public abstract class AbstractAnnotationProcessor
    implements AnnotationProcessor {
    protected Application app;

    protected Reflections reflections;

    public AbstractAnnotationProcessor(Application app) {
        this.app = app;
        this.reflections =
            new Reflections(
                new ConfigurationBuilder()
                    .setUrls(
                        ClasspathHelper.forPackage(Application.getScanPackage())
                    )
                    .setScanners(
                        new TypeAnnotationsScanner(),
                        new MethodAnnotationsScanner(),
                        new FieldAnnotationsScanner(),
                        new SubTypesScanner()
                    )
            );
    }
}
