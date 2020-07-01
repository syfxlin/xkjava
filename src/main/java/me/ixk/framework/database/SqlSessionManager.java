package me.ixk.framework.database;

import java.sql.Connection;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

public interface SqlSessionManager extends SqlSessionFactory, SqlSession {
    void startManagedSession();

    void startManagedSession(boolean autoCommit);

    void startManagedSession(Connection connection);

    void startManagedSession(TransactionIsolationLevel level);

    void startManagedSession(ExecutorType execType);

    void startManagedSession(ExecutorType execType, boolean autoCommit);

    void startManagedSession(
        ExecutorType execType,
        TransactionIsolationLevel level
    );

    void startManagedSession(ExecutorType execType, Connection connection);

    boolean isManagedSessionStarted();

    <T> T getService(Class<T> type);
}
