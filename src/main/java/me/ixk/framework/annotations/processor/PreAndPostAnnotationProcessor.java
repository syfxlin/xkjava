package me.ixk.framework.annotations.processor;

import java.lang.reflect.Method;
import java.util.List;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.PostConstruct;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.ioc.Application;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class PreAndPostAnnotationProcessor extends AbstractAnnotationProcessor {

    public PreAndPostAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        List<Method> postMethods =
            this.getMethodsAnnotatedWith(PostConstruct.class);
        List<Method> preMethods =
            this.getMethodsAnnotatedWith(PreDestroy.class);
        this.app.setAttribute("postConstructs", postMethods);
        this.app.setAttribute("preDestroy", preMethods);
    }
}
