package me.ixk.beans;

import me.ixk.annotations.Aspect;
import me.ixk.annotations.Order;
import me.ixk.aop.AbstractAdvice;
import me.ixk.aop.ProceedingJoinPoint;

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
