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
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import org.apache.ibatis.session.SqlSession;

@Aspect("@annotation(me.ixk.framework.annotations.Transactional)")
public class TransactionalAspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        final SqlSessionManager sqlSessionManager = XkJava
            .of()
            .make(SqlSessionManager.class);
        final Transactional transactional = AnnotationUtils
            .getAnnotation(joinPoint.getMethod())
            .getAnnotation(Transactional.class);
        SqlSession sqlSession = sqlSessionManager.startTransactionSession(
            transactional.isolation()
        );
        try {
            Object result = joinPoint.proceed();
            sqlSession.commit();
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
                sqlSession.rollback();
            }
            throw new TransactionalException("Transactional process error", t);
        } finally {
            sqlSession.close();
        }
    }
}
