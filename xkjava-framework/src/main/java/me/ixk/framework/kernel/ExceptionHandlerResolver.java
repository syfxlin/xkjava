/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理程序解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:28
 */
public class ExceptionHandlerResolver {
    private static final Logger log = LoggerFactory.getLogger(
        ExceptionHandlerResolver.class
    );

    private final Map<Class<? extends Throwable>, Method> methodMap = new ConcurrentHashMap<>();

    public ExceptionHandlerResolver(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            ExceptionHandler exceptionHandler = AnnotationUtils
                .getAnnotation(method)
                .getAnnotation(ExceptionHandler.class);
            if (exceptionHandler != null) {
                for (Class<? extends Throwable> exceptionType : exceptionHandler.exception()) {
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
            log.error(
                "Ambiguous @ExceptionHandler method mapped for [{}]: ({},{})",
                exceptionType,
                oldMethod,
                method
            );
            throw new IllegalStateException(
                "Ambiguous @ExceptionHandler method mapped for [" +
                exceptionType +
                "]: (" +
                oldMethod +
                ", " +
                method +
                ")"
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
        Method method;
        do {
            method = this.getResolverMethod(e.getClass());
            e = e.getCause();
        } while (method == null && e != null);
        return method;
    }
}
