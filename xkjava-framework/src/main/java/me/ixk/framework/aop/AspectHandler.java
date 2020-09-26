/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

public class AspectHandler {
    protected final Object target;

    protected final Method method;

    protected final MethodProxy methodProxy;

    protected final Object[] args;

    protected int currAspectIndex;

    protected final List<Advice> aspects;

    protected Advice aspect = null;

    protected Throwable error = null;

    public AspectHandler(
        Object target,
        Method method,
        MethodProxy methodProxy,
        Object[] args,
        int currAspectIndex,
        List<Advice> aspects
    ) {
        this.target = target;
        this.method = method;
        this.methodProxy = methodProxy;
        this.args = args;
        this.currAspectIndex = currAspectIndex;
        this.aspects = aspects;
        if (this.hasNextAspect()) {
            this.aspect = this.getNextAspect();
        }
    }

    protected boolean hasNextAspect() {
        return this.aspects.size() > currAspectIndex;
    }

    protected Advice getNextAspect() {
        return this.aspects.get(currAspectIndex++);
    }

    public Object invokeAspect() throws Throwable {
        if (aspect == null) {
            return this.methodProxy.invoke(this.target, this.args);
        }
        Object result = null;
        try {
            // Around
            result = this.aspect.around(this.makeProceedingJoinPoint());
        } catch (Throwable e) {
            this.error = e;
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
        return this.methodProxy.invoke(
                this.target,
                args.length == 0 ? this.args : args
            );
    }

    public Object invokeNext() throws Throwable {
        AspectHandler handler = new AspectHandler(
            this.target,
            this.method,
            this.methodProxy,
            this.args,
            currAspectIndex,
            this.aspects
        );
        return handler.invokeAspect();
    }

    public ProceedingJoinPoint makeProceedingJoinPoint() {
        ProceedingJoinPoint point = new ProceedingJoinPoint(
            this,
            this.target,
            this.method,
            this.methodProxy,
            this.args
        );
        if (this.error != null) {
            point.setError(this.error);
        }
        return point;
    }

    public JoinPoint makeJoinPoint() {
        return this.makeJoinPoint(null);
    }

    public JoinPoint makeJoinPoint(Object _return) {
        JoinPoint point = new JoinPoint(
            this,
            this.target,
            this.method,
            this.methodProxy,
            this.args
        );
        if (this.error != null) {
            point.setError(this.error);
        }
        if (_return != null) {
            point.setReturn(_return);
        }
        return point;
    }
}
