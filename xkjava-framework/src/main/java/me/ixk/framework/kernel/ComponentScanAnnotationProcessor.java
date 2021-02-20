/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.ixk.framework.annotation.ComponentScan;
import me.ixk.framework.ioc.BeanScanner;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.processor.AbstractAnnotationProcessor;
import me.ixk.framework.util.MergedAnnotation;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2020/12/1 上午 8:44
 */
public class ComponentScanAnnotationProcessor
    extends AbstractAnnotationProcessor {

    private static final Logger log = LoggerFactory.getLogger(
        ComponentScanAnnotationProcessor.class
    );
    private static final String[] DEFAULT_DEFINITION = new String[] {
        "me.ixk.framework",
    };
    private final BeanScanner scanner;

    public ComponentScanAnnotationProcessor(final XkJava app) {
        super(app);
        this.scanner = app.beanScanner();
    }

    @Override
    public void process() {
        this.scanner.addDefinition(DEFAULT_DEFINITION);
        for (final Class<?> source : this.app.primarySource()) {
            this.scanner.addDefinition(
                    new String[] { source.getPackageName() }
                );
            for (final ComponentScan scan : MergedAnnotation
                .from(source)
                .getAnnotations(ComponentScan.class)) {
                this.loadPackageScanAnnotationItem(scan);
            }
        }
        final Reflections reflections = this.scanner.getReflections();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(
            ComponentScan.class
        )) {
            for (ComponentScan scan : MergedAnnotation
                .from(clazz)
                .getAnnotations(ComponentScan.class)) {
                this.loadPackageScanAnnotationItem(scan);
            }
        }
        for (Method method : reflections.getMethodsAnnotatedWith(
            ComponentScan.class
        )) {
            for (ComponentScan scan : MergedAnnotation
                .from(method)
                .getAnnotations(ComponentScan.class)) {
                this.loadPackageScanAnnotationItem(scan);
            }
        }
    }

    private void loadPackageScanAnnotationItem(
        final ComponentScan componentScan
    ) {
        this.scanner.addDefinition(componentScan);
        if (log.isDebugEnabled()) {
            log.debug(
                "Application add base packages: {}",
                Arrays.toString(componentScan.basePackages())
            );
        }
    }
}
