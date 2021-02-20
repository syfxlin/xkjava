/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aspect;

import me.ixk.framework.annotation.Aspect;
import me.ixk.framework.annotation.Transactional;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.aop.ProceedingJoinPoint;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.exception.TransactionalException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事务切面
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:40
 */
@Aspect("@annotation(me.ixk.framework.annotation.Transactional)")
public class TransactionalAspect implements Advice {

    private static final Logger log = LoggerFactory.getLogger(
        TransactionalAspect.class
    );

    private final SqlSessionManager sqlSessionManager;

    public TransactionalAspect(SqlSessionManager sqlSessionManager) {
        this.sqlSessionManager = sqlSessionManager;
    }

    @Override
    public Object around(final ProceedingJoinPoint joinPoint) {
        final Transactional transactional = joinPoint
            .getMethodAnnotation()
            .getAnnotation(Transactional.class);
        // 开启对应事务隔离级别的 SqlSession，并存入到 ThreadLocal，用于下一步操作的时候能使用这个 SqlSession
        final SqlSession sqlSession =
            this.sqlSessionManager.startTransactionSession(
                    transactional.isolation()
                );
        try {
            // 处理代理方法
            final Object result = joinPoint.proceed();
            // 如果没抛出异常就提交事务，然后返回值
            sqlSession.commit();
            return result;
        } catch (final Throwable t) {
            log.error("Transactional throws exception", t);
            // 如果抛出异常了，判断是否回滚异常，只有 Error 和 RuntimeException 的子类能被处理
            boolean rollback =
                t instanceof Error || t instanceof RuntimeException;
            // 是否在回滚异常列表
            for (final Class<? extends Exception> rollbackFor : transactional.rollbackFor()) {
                if (rollbackFor.isAssignableFrom(t.getClass())) {
                    rollback = true;
                    break;
                }
            }
            // 如果是则回滚事务
            if (rollback) {
                sqlSession.rollback();
                log.error("Rollback success");
            } else {
                // 否则抛出异常
                throw new TransactionalException(
                    "Transactional process error",
                    t
                );
            }
        } finally {
            // 关闭全局 SqlSession
            sqlSession.close();
        }
        return null;
    }
}
