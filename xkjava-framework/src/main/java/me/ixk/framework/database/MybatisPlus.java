/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.database;

import cn.hutool.core.util.ReflectUtil;
import java.sql.Connection;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MybatisPlus
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:58
 */
public class MybatisPlus
    implements me.ixk.framework.database.SqlSessionManager {
    private static final Logger log = LoggerFactory.getLogger(
        MybatisPlus.class
    );
    private final org.apache.ibatis.session.SqlSessionManager sqlSessionManager;

    // 当全局 SqlSession 被设置的时候就认为是开启了事务

    public MybatisPlus(
        org.apache.ibatis.session.SqlSessionManager sqlSessionManager
    ) {
        this.sqlSessionManager = sqlSessionManager;
    }

    @Override
    public SqlSession openSession() {
        return this.sqlSessionManager.openSession();
    }

    @Override
    public SqlSession openSession(final boolean autoCommit) {
        return this.sqlSessionManager.openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(final Connection connection) {
        return this.sqlSessionManager.openSession(connection);
    }

    @Override
    public SqlSession openSession(final TransactionIsolationLevel level) {
        return this.sqlSessionManager.openSession(level);
    }

    @Override
    public SqlSession openSession(final ExecutorType execType) {
        return this.sqlSessionManager.openSession(execType);
    }

    @Override
    public SqlSession openSession(
        final ExecutorType execType,
        final boolean autoCommit
    ) {
        return this.sqlSessionManager.openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(
        final ExecutorType execType,
        final TransactionIsolationLevel level
    ) {
        return this.sqlSessionManager.openSession(execType, level);
    }

    @Override
    public SqlSession openSession(
        final ExecutorType execType,
        final Connection connection
    ) {
        return this.sqlSessionManager.openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return this.sqlSessionManager.getConfiguration();
    }

    @Override
    public <T> T getService(final Class<T> type) {
        return ReflectUtil.newInstance(type);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.sqlSessionManager.getMapper(type);
    }

    @Override
    public SqlSession startTransactionSession(TransactionIsolationLevel level) {
        log.debug("Start transaction session: level {}", level);
        this.sqlSessionManager.startManagedSession(level);
        return this.sqlSessionManager;
    }

    @Override
    public boolean isTransactionSession() {
        return this.sqlSessionManager.isManagedSessionStarted();
    }
}
