/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.database;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

public interface SqlSessionManager extends SqlSessionFactory {
    <T> T getMapper(Class<T> type);

    <T> T getService(Class<T> type);

    SqlSession startTransactionSession(TransactionIsolationLevel level);

    boolean isTransactionSession();
}
