/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aspect;

import me.ixk.framework.annotations.Aspect;
import me.ixk.framework.annotations.Transactional;
import me.ixk.framework.aop.AbstractAdvice;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.exceptions.TransactionalException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;

@Aspect("@annotation(me.ixk.framework.annotations.Transactional)")
public class TransactionalAspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        final SqlSessionManager sqlSessionManager = Application
            .get()
            .make(SqlSessionManager.class);
        final Transactional transactional = AnnotationUtils.getAnnotation(
            joinPoint.getMethod(),
            Transactional.class
        );
        try {
            sqlSessionManager.startManagedSession(transactional.isolation());
            Object result = joinPoint.proceed();
            sqlSessionManager.commit();
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
                sqlSessionManager.rollback();
            }
            throw new TransactionalException("Transactional process error", t);
        } finally {
            sqlSessionManager.close();
        }
    }
}
