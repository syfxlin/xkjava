/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.exception.DispatchServletException;
import me.ixk.framework.exception.Exception;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.binder.DefaultDataBinder;
import me.ixk.framework.registry.after.ExceptionHandlerRegistry;
import me.ixk.framework.web.ExceptionInfo;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理程序解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:28
 */
@WebResolver
public class ExceptionHandlerResolver implements HandlerExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(
        ExceptionHandlerResolver.class
    );

    private final XkJava app;
    private final ExceptionHandlerRegistry registry;

    public ExceptionHandlerResolver(
        XkJava app,
        ExceptionHandlerRegistry registry
    ) {
        this.app = app;
        this.registry = registry;
    }

    @Override
    public Object resolveException(
        Throwable e,
        ExceptionInfo info,
        WebContext context,
        WebDataBinder binder
    ) {
        Object result = NO_RESOLVER;
        final Class<?> handlerType = info.getHandlerType();
        final Map<Class<? extends Throwable>, Method> resolver =
            this.registry.getControllerResolver(handlerType);
        if (resolver != null) {
            result = this.processException(e, resolver);
            if (result != NO_RESOLVER) {
                return result;
            }
        }
        for (final Map.Entry<Class<?>, Map<Class<? extends Throwable>, Method>> entry : registry
            .getAdviceResolvers()
            .entrySet()) {
            result = this.processException(e, entry.getValue());
            if (result != NO_RESOLVER) {
                return result;
            }
        }

        return result;
    }

    private Object processException(
        final Throwable exception,
        final Map<Class<? extends Throwable>, Method> resolver
    ) {
        try {
            final Method method = this.resolveMethod(exception, resolver);
            if (method != null) {
                // 绑定可能注入的异常
                final Map<String, Object> args = new ConcurrentHashMap<>(10);
                args.put("exception", exception);
                args.put(exception.getClass().getName(), exception);
                args.put(Throwable.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                args.put(Exception.class.getName(), exception);
                // 获取返回值
                return this.app.call(
                        method,
                        new DefaultDataBinder(this.app, args)
                    );
            }
        } catch (final Throwable e) {
            throw new DispatchServletException(
                "Process ExceptionHandlerResolver failed",
                e
            );
        }
        return NO_RESOLVER;
    }

    private Method getResolverMethod(
        Class<? extends Throwable> exceptionType,
        Map<Class<? extends Throwable>, Method> resolver
    ) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : resolver.keySet()) {
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
            return resolver.get(matches.get(0));
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

    private Method resolveMethod(
        Throwable e,
        Map<Class<? extends Throwable>, Method> resolver
    ) {
        Method method;
        do {
            method = this.getResolverMethod(e.getClass(), resolver);
            e = e.getCause();
        } while (method == null && e != null);
        return method;
    }
}
