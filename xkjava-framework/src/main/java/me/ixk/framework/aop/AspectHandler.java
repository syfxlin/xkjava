/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.ixk.framework.ioc.entity.AnnotatedEntry;
import me.ixk.framework.ioc.entity.ParameterContext.ParameterEntry;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ParameterNameDiscoverer;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 切面处理器
 * <p>
 * 采用责任链模式处理多个切面
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:15
 */
public class AspectHandler {

    private static final Logger log = LoggerFactory.getLogger(
        AspectHandler.class
    );

    /**
     * 被代理对象的信息
     */
    private final TargetInfo info;
    /**
     * 与当前方法匹配的所有切面
     */
    private final List<Advice> aspects;
    /**
     * 当前执行到的切面索引
     */
    private final AtomicInteger currAspectIndex;
    /**
     * 用于临时存储当前切面
     */
    private volatile Advice aspect = null;
    /**
     * 切面中抛出的异常
     */
    private volatile Throwable error = null;

    public AspectHandler(
        TargetInfo info,
        AtomicInteger currAspectIndex,
        List<Advice> aspects
    ) {
        this.info = info;
        this.currAspectIndex = currAspectIndex;
        this.aspects = aspects;
        if (this.hasNextAspect()) {
            this.aspect = this.getNextAspect();
        }
    }

    private boolean hasNextAspect() {
        return this.aspects.size() > currAspectIndex.get();
    }

    private Advice getNextAspect() {
        return this.aspects.get(currAspectIndex.getAndIncrement());
    }

    public Object invokeAspect() throws Throwable {
        if (aspect == null) {
            // 当切面不存在的时候说明执行完毕或没有切面，则执行原始方法
            return this.info.getMethodProxy()
                .invoke(this.info.getTarget(), this.info.getArgs());
        }
        Object result = null;
        try {
            // Around
            result = this.aspect.around(this.makeProceedingJoinPoint());
        } catch (Throwable e) {
            // 抛出异常的时候保存异常
            this.error = e;
            log.error(
                "Aspect [" +
                this.aspect.getClass().getName() +
                "] invoke throws exception: ",
                e
            );
        }
        // After
        this.aspect.after(this.makeJoinPoint(result));
        // After*
        if (this.error != null) {
            this.aspect.afterThrowing(this.error);
        } else {
            this.aspect.afterReturning(this.makeJoinPoint(result));
        }
        return result;
    }

    public Object invokeProcess(Object[] args) throws Throwable {
        // Before
        this.aspect.before(this.makeJoinPoint());
        if (this.hasNextAspect()) {
            return this.invokeNext();
        }
        return this.info.getMethodProxy()
            .invoke(
                this.info.getTarget(),
                args.length == 0 ? this.info.getArgs() : args
            );
    }

    public Object invokeNext() throws Throwable {
        AspectHandler handler = new AspectHandler(
            this.info,
            currAspectIndex,
            this.aspects
        );
        return handler.invokeAspect();
    }

    public ProceedingJoinPoint makeProceedingJoinPoint() {
        ProceedingJoinPoint point = new ProceedingJoinPoint(this, this.info);
        if (this.error != null) {
            point.setError(this.error);
        }
        return point;
    }

    public JoinPoint makeJoinPoint() {
        return this.makeJoinPoint(null);
    }

    public JoinPoint makeJoinPoint(Object returnValue) {
        JoinPoint point = new JoinPoint(this, this.info);
        if (this.error != null) {
            point.setError(this.error);
        }
        if (returnValue != null) {
            point.setReturn(returnValue);
        }
        return point;
    }

    public static class TargetInfo {

        private final Object target;
        private final Object[] args;
        private final MethodProxy methodProxy;

        private final AnnotatedEntry<Class<?>> clazzEntry;
        private final AnnotatedEntry<Method> methodEntry;
        private final ParameterEntry[] parameterEntries;

        public TargetInfo(
            Object target,
            Class<?> clazz,
            Method method,
            MethodProxy methodProxy,
            Object[] args
        ) {
            this.target = target;
            this.clazzEntry = new AnnotatedEntry<>(clazz);
            this.methodEntry = new AnnotatedEntry<>(method);
            this.methodProxy = methodProxy;
            this.args = args;
            final Parameter[] parameters = method.getParameters();
            final String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
                method
            );
            this.parameterEntries = new ParameterEntry[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                this.parameterEntries[i] =
                    new ParameterEntry(parameters[i], parameterNames[i]);
            }
        }

        public Object getTarget() {
            return target;
        }

        public Object[] getArgs() {
            return args;
        }

        public MethodProxy getMethodProxy() {
            return methodProxy;
        }

        public Class<?> getClazz() {
            return clazzEntry.getElement();
        }

        public MergedAnnotation getClassAnnotation() {
            return clazzEntry.getAnnotation();
        }

        public Method getMethod() {
            return methodEntry.getElement();
        }

        public MergedAnnotation getMethodAnnotation() {
            return methodEntry.getAnnotation();
        }

        public ParameterEntry[] getParameterEntries() {
            return parameterEntries;
        }
    }
}
