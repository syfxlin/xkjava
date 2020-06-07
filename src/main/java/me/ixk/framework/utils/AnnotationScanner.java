package me.ixk.framework.utils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.ConcurrentHashSet;

public class AnnotationScanner {

    public static Map<Class<? extends Annotation>, Set<Class<?>>> scan(
        String _package
    ) {
        return scan(Collections.singletonList(_package));
    }

    public static Map<Class<? extends Annotation>, Set<Class<?>>> scan(
        String[] packages
    ) {
        return scan(Arrays.asList(packages));
    }

    public static Map<Class<? extends Annotation>, Set<Class<?>>> scan(
        List<String> packages
    ) {
        Map<Class<? extends Annotation>, Set<Class<?>>> annotations = new ConcurrentHashMap<>();
        for (String basePackage : packages) {
            for (Class<?> _class : ClassUtil.getPackageClass(basePackage)) {
                for (Annotation annotation : _class.getAnnotations()) {
                    Class<? extends Annotation> annotationClass = annotation.annotationType();
                    Set<Class<?>> list = annotations.getOrDefault(
                        annotationClass,
                        new ConcurrentHashSet<>()
                    );
                    list.add(_class);
                    annotations.put(annotationClass, list);
                }
            }
        }
        return annotations;
    }
}
