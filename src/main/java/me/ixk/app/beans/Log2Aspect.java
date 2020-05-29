package me.ixk.app.beans;

import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.aop.AbstractAdvice;
import me.ixk.framework.aop.ProceedingJoinPoint;

@Aspect("@annotation(me.ixk.annotations.Log2)")
@Order(1)
public class Log2Aspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        System.out.println("Before");
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("After");
        return result;
    }
}
