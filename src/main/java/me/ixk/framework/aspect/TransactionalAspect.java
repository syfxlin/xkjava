package me.ixk.framework.aspect;

import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Transactional;
import me.ixk.framework.aop.AbstractAdvice;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.exceptions.TransactionalException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.MybatisPlus;

@Aspect("@annotation(me.ixk.framework.annotations.Transactional)")
public class TransactionalAspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        final MybatisPlus mybatisPlus = Application
            .get()
            .make(MybatisPlus.class);
        final Transactional transactional = joinPoint
            .getMethod()
            .getAnnotation(Transactional.class);
        try {
            mybatisPlus.startManagedSession(transactional.isolation());
            Object result = joinPoint.proceed();
            mybatisPlus.commit();
            return result;
        } catch (Throwable t) {
            boolean rollback =
                t instanceof Error || t instanceof RuntimeException;
            for (Class<? extends Exception> rollbackFor : transactional.rollbackFor()) {
                if (rollbackFor.isAssignableFrom(t.getClass())) {
                    rollback = true;
                    break;
                }
            }
            if (rollback) {
                mybatisPlus.rollback();
            }
            throw new TransactionalException("Transactional process error", t);
        } finally {
            mybatisPlus.close();
        }
    }
}
