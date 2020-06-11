package me.ixk.framework.annotations.processor;

import java.util.Set;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.aop.AspectPointcut;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.ReflectionsUtils;

public class AspectAnnotationProcessor extends AbstractAnnotationProcessor {

    public AspectAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        Set<Class<?>> classes = ReflectionsUtils
            .make(Application.getScanPackage())
            .getTypesAnnotatedWith(Aspect.class);
        for (Class<?> _class : classes) {
            if (Advice.class.isAssignableFrom(_class)) {
                Aspect aspect = _class.getAnnotation(Aspect.class);
                AspectManager.addAdvice(
                    new AspectPointcut(aspect.value()),
                    this.app.make(_class.getName(), Advice.class)
                );
            }
        }
    }
}
