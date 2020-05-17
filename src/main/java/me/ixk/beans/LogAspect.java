package me.ixk.beans;

import me.ixk.annotations.Aspect;
import me.ixk.aop.AbstractAdvice;
import me.ixk.aop.ProceedingJoinPoint;

@Aspect("@annotation(me.ixk.annotations.Log2)")
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
