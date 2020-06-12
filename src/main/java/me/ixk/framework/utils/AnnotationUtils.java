package me.ixk.framework.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;
import me.ixk.framework.annotations.AliasFor;

public abstract class AnnotationUtils {

    public static <T extends Annotation> T getAnnotation(
        Field field,
        Class<T> annotationType
    ) {
        return parseAnnotation(field.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Constructor<?> constructor,
        Class<T> annotationType
    ) {
        return parseAnnotation(constructor.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Method method,
        Class<T> annotationType
    ) {
        return parseAnnotation(method.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T getAnnotation(
        Class<?> _class,
        Class<T> annotationType
    ) {
        return parseAnnotation(_class.getAnnotation(annotationType));
    }

    public static <T extends Annotation> T parseAnnotation(T annotation) {
        if (annotation == null) {
            return null;
        }
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        Map<String, Object> memberValues = getMemberValues(annotation);
        Method[] methodValues = annotationClass.getMethods();
        for (Method method : methodValues) {
            if (method.getAnnotation(AliasFor.class) == null) {
                continue;
            }
            String name = method.getName();
            if (
                method
                    .getDefaultValue()
                    .equals(memberValues.get(method.getName()))
            ) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                String alias = aliasFor.value();
                if (aliasFor.annotation() != Annotation.class) {
                    Object object = getAnnotationValue(
                        parseAnnotation(
                            annotationClass.getAnnotation(aliasFor.annotation())
                        ),
                        alias.length() == 0 ? name : alias
                    );
                    memberValues.put(name, object);
                } else {
                    memberValues.put(name, memberValues.get(alias));
                }
            }
        }
        return annotation;
    }

    public static Object getAnnotationValue(Annotation annotation, String key) {
        try {
            return annotation
                .annotationType()
                .getMethod(key)
                .invoke(annotation);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMemberValues(Annotation annotation) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(
                annotation
            );
            Field field = invocationHandler
                .getClass()
                .getDeclaredField("memberValues");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(invocationHandler);
        } catch (Exception e) {
            throw new RuntimeException("Get annotation member values failed");
        }
    }
}
