package me.ixk.app.beans;

import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.aop.AbstractAdvice;
import me.ixk.framework.aop.ProceedingJoinPoint;

@Aspect("@annotation(me.ixk.app.annotations.Log)")
public class LogAspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        System.out.println("Before-Log");
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("After-Log");
        return result;
    }
}
