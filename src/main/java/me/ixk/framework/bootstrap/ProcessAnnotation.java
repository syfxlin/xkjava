package me.ixk.framework.bootstrap;

import java.util.List;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.utils.ReflectionsUtils;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class ProcessAnnotation extends AbstractBootstrap {

    public ProcessAnnotation(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        List<Class<?>> processors = ReflectionsUtils.getTypesAnnotatedWith(
            me.ixk.framework.annotations.AnnotationProcessor.class
        );
        for (Class<?> processorType : processors) {
            if (!AnnotationProcessor.class.isAssignableFrom(processorType)) {
                throw new AnnotationProcessorException(
                    "Classes marked by the AnnotationProcessor annotation should implement the AnnotationProcessor interface"
                );
            }
        }
        AnnotationProcessorManager manager = new AnnotationProcessorManager(
            this.app
        );
        this.app.setAnnotationProcessorManager(manager);
        this.app.instance(
                AnnotationProcessor.class,
                manager,
                "annotationProcessorManager"
            );
        manager.registers(processors);
        manager.process();
    }
}
