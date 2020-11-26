/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.scheduling;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Async;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步切面
 *
 * @author Otstar Lin
 * @date 2020/11/26 上午 10:34
 */
@Aspect("@annotation(me.ixk.framework.annotations.Async)")
public class AsyncAspect implements Advice {
    private static final Logger log = LoggerFactory.getLogger(
        AsyncAspect.class
    );

    @Autowired
    public XkJava app;

    @Override
    public Object around(final ProceedingJoinPoint joinPoint) {
        final Method method = joinPoint.getMethod();
        final Async async = AnnotationUtils.getAnnotation(method, Async.class);
        AsyncTaskExecutor executor = this.app.make(AsyncTaskExecutor.class);
        if (executor == null) {
            throw new NullPointerException(
                "No executor specified and no default executor set on async task"
            );
        }

        Callable<Object> task = () -> {
            try {
                Object result = joinPoint.proceed();
                if (result instanceof Future) {
                    return ((Future<?>) result).get();
                }
            } catch (ExecutionException ex) {
                handleError(ex.getCause(), method, joinPoint.getArgs());
            } catch (Throwable ex) {
                handleError(ex, method, joinPoint.getArgs());
            }
            return null;
        };

        return this.submit(task, executor, method.getReturnType());
    }

    private Object submit(
        Callable<Object> task,
        AsyncTaskExecutor executor,
        Class<?> returnType
    ) {
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return task.call();
                    } catch (Throwable ex) {
                        throw new CompletionException(ex);
                    }
                },
                executor
            );
        } else if (Future.class.isAssignableFrom(returnType)) {
            return executor.submit(task);
        } else {
            executor.submit(task);
            return null;
        }
    }

    private void handleError(Throwable ex, Method method, Object... params)
        throws Exception {
        if (Future.class.isAssignableFrom(method.getReturnType())) {
            if (ex instanceof Exception) {
                throw (Exception) ex;
            } else if (ex instanceof Error) {
                throw (Error) ex;
            } else {
                throw new UndeclaredThrowableException(ex);
            }
        } else {
            log.error(
                "Unexpected exception occurred invoking async method: " +
                method,
                ex
            );
        }
    }
}
