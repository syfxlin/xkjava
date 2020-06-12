package me.ixk.framework.annotations.processor;

import java.util.Set;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.ioc.Application;

public class ControllerAdviceAnnotationProcessor
    extends AbstractAnnotationProcessor {

    public ControllerAdviceAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        Set<Class<?>> classes =
            this.reflections.getTypesAnnotatedWith(ControllerAdvice.class);
    }
}
