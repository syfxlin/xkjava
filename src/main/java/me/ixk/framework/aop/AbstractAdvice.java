package me.ixk.framework.aop;

public abstract class AbstractAdvice implements Advice {

    @Override
    public void before(JoinPoint joinPoint) {}

    @Override
    public void after(JoinPoint joinPoint) {}

    @Override
    public void afterReturning(JoinPoint joinPoint) {}

    @Override
    public void afterThrowing(Throwable exception) throws Throwable {
        throw exception;
    }

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
