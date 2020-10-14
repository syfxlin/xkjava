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

/**
 * 事务切面
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:40
 */
@Aspect("@annotation(me.ixk.framework.annotations.Transactional)")
public class TransactionalAspect extends AbstractAdvice {

    @Override
    public Object around(ProceedingJoinPoint joinPoint) {
        // SqlSession 管理器
        final SqlSessionManager sqlSessionManager = XkJava
            .of()
            .make(SqlSessionManager.class);
        final Transactional transactional = AnnotationUtils
            .getAnnotation(joinPoint.getMethod())
            .getAnnotation(Transactional.class);
        // 开启对应事务隔离级别的 SqlSession，并存入到 ThreadLocal，用于下一步操作的时候能使用这个 SqlSession
        SqlSession sqlSession = sqlSessionManager.startTransactionSession(
            transactional.isolation()
        );
        try {
            // 处理代理方法
            Object result = joinPoint.proceed();
            // 如果没抛出异常就提交事务，然后返回值
            sqlSession.commit();
            return result;
        } catch (Throwable t) {
            // 如果抛出异常了，判断是否回滚异常，只有 Error 和 RuntimeException 的子类能被处理
            boolean rollback =
                t instanceof Error || t instanceof RuntimeException;
            // 是否在回滚异常列表
            for (Class<? extends Exception> rollbackFor : transactional.rollbackFor()) {
                if (rollbackFor.isAssignableFrom(t.getClass())) {
                    rollback = true;
                    break;
                }
            }
            // 如果是则回滚事务
            if (rollback) {
                sqlSession.rollback();
            }
            // 否则抛出异常
            throw new TransactionalException("Transactional process error", t);
        } finally {
            // 关闭全局 SqlSession
            sqlSession.close();
        }
    }
}
