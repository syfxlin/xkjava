package me.ixk.aop;

public interface Advice {
    void before(JoinPoint joinPoint);

    void after(JoinPoint joinPoint);

    void afterReturning(JoinPoint joinPoint);

    void afterThrowing(Throwable exception) throws Throwable;

    Object around(ProceedingJoinPoint joinPoint);
}
