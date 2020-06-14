package me.ixk.framework.annotations.processor;

import me.ixk.framework.bootstrap.Bootstrap;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;

import java.util.List;

public class BootstrapAnnotationProcessor extends AbstractAnnotationProcessor {

    public BootstrapAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        List<Class<?>> bootstraps =
            this.getTypesAnnotatedWith(
                    me.ixk.framework.annotations.Bootstrap.class
                );
        for (Class<?> bootstrapType : bootstraps) {
            if (Bootstrap.class.isAssignableFrom(bootstrapType)) {
                this.app.call(bootstrapType, "boot", Object.class);
            } else {
                throw new AnnotationProcessorException(
                    "Classes marked by the Bootstrap annotation should implement the Bootstrap interface"
                );
            }
        }
    }
}
