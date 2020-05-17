package me.ixk.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.util.List;

public class AspectHandler {
    protected Object target;

    protected MethodProxy method;

    protected Object[] args;

    protected List<AdviceInterface> aspects;

    protected AdviceInterface aspect = null;

    protected Throwable error = null;

    public AspectHandler(
        Object target,
        MethodProxy method,
        Object[] args,
        List<AdviceInterface> aspects
    ) {
        this.target = target;
        this.method = method;
        this.args = args;
        if (!aspects.isEmpty()) {
            this.aspect = aspects.remove(0);
        }
        this.aspects = aspects;
    }

    public Object invokeAspect() throws Throwable {
        if (aspect == null) {
            return this.method.invokeSuper(this.target, this.args);
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
        if (!this.aspects.isEmpty()) {
            return this.invokeNext();
        }
        return this.method.invokeSuper(
                this.target,
                args.length == 0 ? this.args : args
            );
    }

    public Object invokeNext() throws Throwable {
        AspectHandler handler = new AspectHandler(
            this.target,
            this.method,
            this.args,
            this.aspects
        );
        return handler.invokeAspect();
    }

    public ProceedingJoinPoint makeProceedingJoinPoint() {
        ProceedingJoinPoint point = new ProceedingJoinPoint(
            this,
            this.target,
            this.method,
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
