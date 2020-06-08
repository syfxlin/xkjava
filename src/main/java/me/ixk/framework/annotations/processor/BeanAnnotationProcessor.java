package me.ixk.framework.annotations.processor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import me.ixk.framework.annotations.*;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import org.eclipse.jetty.util.ConcurrentHashSet;

public class BeanAnnotationProcessor extends AbstractAnnotationProcessor {
    protected List<Class<? extends Annotation>> aliasAnnotations = Arrays.asList(
        Bean.class,
        Component.class,
        Controller.class,
        Repository.class,
        Service.class
    );

    public BeanAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() {
        Set<Class<?>> scopes =
            this.annotations.getOrDefault(
                    Scope.class,
                    new ConcurrentHashSet<>()
                );
        List<Class<? extends Annotation>> beanAnnotations = Config.get(
            "app.bean_annotations",
            List.class
        );
        for (Class<? extends Annotation> annotation : beanAnnotations) {
            for (Class<?> _class : this.annotations.getOrDefault(
                    annotation,
                    new ConcurrentHashSet<>()
                )) {
                boolean isShared = true;
                if (scopes.contains(_class)) {
                    isShared =
                        _class
                            .getAnnotation(Scope.class)
                            .value()
                            .equals("singleton");
                }
                if (this.aliasAnnotations.contains(annotation)) {
                    this.processAliasAnnotation(
                            _class.getAnnotation(annotation),
                            _class,
                            isShared
                        );
                } else {
                    this.processNoAliasAnnotation(_class, isShared);
                }
            }
        }
    }

    public void processAliasAnnotation(
        Annotation annotation,
        Class<?> _class,
        boolean isShared
    ) {
        try {
            String[] names = (String[]) annotation
                .getClass()
                .getMethod("value")
                .invoke(annotation);
            this.app.bind(_class, _class, isShared);
            for (String name : names) {
                this.app.bind(_class, _class, isShared, name);
            }
        } catch (Exception e) {
            // no code
        }
    }

    public void processNoAliasAnnotation(Class<?> _class, boolean isShared) {
        this.app.bind(_class, _class, isShared);
    }
}