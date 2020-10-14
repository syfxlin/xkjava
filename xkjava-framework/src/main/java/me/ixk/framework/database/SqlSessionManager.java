/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.database;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 * SqlSession 管理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:59
 */
public interface SqlSessionManager extends SqlSessionFactory {
    /**
     * 获取 Mapper
     *
     * @param type Mapper 类型
     * @param <T>  Mapper 泛型
     *
     * @return Mapper 对象
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取 Service
     *
     * @param type Service 类型
     * @param <T>  Service 泛型
     *
     * @return Service 对象
     */
    <T> T getService(Class<T> type);

    /**
     * 开始事务 Session
     *
     * @param level 事务隔离级别
     *
     * @return 事务 SqlSession
     */
    SqlSession startTransactionSession(TransactionIsolationLevel level);

    /**
     * 是否开启了事务
     *
     * @return 是否开启了事务
     */
    boolean isTransactionSession();
}
