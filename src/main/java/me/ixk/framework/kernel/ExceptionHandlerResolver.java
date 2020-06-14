package me.ixk.framework.kernel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.utils.AnnotationUtils;

public class ExceptionHandlerResolver {
    private final Map<Class<? extends Throwable>, Method> methodMap = new ConcurrentHashMap<>();

    public ExceptionHandlerResolver(Class<?> _class) {
        for (Method method : _class.getDeclaredMethods()) {
            ExceptionHandler exceptionHandler = AnnotationUtils.getAnnotation(
                method,
                ExceptionHandler.class
            );
            if (exceptionHandler != null) {
                for (Class<? extends Throwable> exceptionType : exceptionHandler.value()) {
                    this.addExceptionMapping(exceptionType, method);
                }
            }
        }
    }

    private void addExceptionMapping(
        Class<? extends Throwable> exceptionType,
        Method method
    ) {
        Method oldMethod = this.methodMap.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException(
                "Ambiguous @ExceptionHandler method mapped for [" +
                exceptionType +
                "]: {" +
                oldMethod +
                ", " +
                method +
                "}"
            );
        }
    }

    public boolean hasExceptionMappings() {
        return !this.methodMap.isEmpty();
    }

    private Method getResolverMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.methodMap.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            matches.sort(
                Comparator.comparingInt(
                    o -> exceptionDepth(o, exceptionType, 0)
                )
            );
            return this.methodMap.get(matches.get(0));
        } else {
            return null;
        }
    }

    private int exceptionDepth(
        Class<?> declaredException,
        Class<?> exceptionToMatch,
        int depth
    ) {
        if (exceptionToMatch.equals(declaredException)) {
            return depth;
        }
        if (exceptionToMatch == Throwable.class) {
            return Integer.MAX_VALUE;
        }
        return exceptionDepth(
            declaredException,
            exceptionToMatch.getSuperclass(),
            depth + 1
        );
    }

    public Method resolveMethod(Throwable e) {
        Method method = this.getResolverMethod(e.getClass());
        if (method == null) {
            Throwable cause = e.getCause();
            if (cause != null) {
                method = this.getResolverMethod(e.getClass());
            }
        }
        return method;
    }
}