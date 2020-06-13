package me.ixk.framework.annotations.processor;

import java.util.List;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

public class AspectAnnotationProcessor extends AbstractAnnotationProcessor {

    public AspectAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        List<Class<?>> classes = this.getTypesAnnotatedWith(Aspect.class);
        for (Class<?> _class : classes) {
            if (Advice.class.isAssignableFrom(_class)) {
                Aspect aspect = AnnotationUtils.getAnnotation(
                    _class,
                    Aspect.class
                );
                AspectManager.addAdvice(
                    new AspectPointcut(aspect.value()),
                    this.app.make(_class.getName(), Advice.class)
                );
            }
        }
    }
}
