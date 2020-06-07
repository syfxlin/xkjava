package me.ixk.framework.bootstrap;

import java.util.List;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.AnnotationProcessorManager;

public class ProcessAnnotation extends AbstractBootstrap {

    public ProcessAnnotation(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void boot() {
        AnnotationProcessorManager manager = new AnnotationProcessorManager(
            this.app
        );
        this.app.setAnnotationProcessorManager(manager);
        this.app.instance(
                AnnotationProcessor.class,
                manager,
                "annotationProcessorManager"
            );
        manager.registers(Config.get("app.annotation_processors", List.class));
        manager.process();
    }
}
