package me.ixk.framework.utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import me.ixk.framework.exceptions.MybatisPlusException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MybatisPlus {
    protected final SqlSessionManager sessionManager;

    public MybatisPlus(DataSource dataSource, List<String> mapperPackages) {
        SqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        Environment environment = new Environment(
            "database",
            new JdbcTransactionFactory(),
            dataSource
        );
        Configuration configuration = new MybatisConfiguration(environment);
        for (String mapperPackage : mapperPackages) {
            configuration.addMappers(mapperPackage);
        }
        this.sessionManager =
            SqlSessionManager.newInstance(builder.build(configuration));
    }

    public SqlSessionManager getSessionManager() {
        if (!this.sessionManager.isManagedSessionStarted()) {
            this.sessionManager.startManagedSession(true);
        }
        return this.sessionManager;
    }

    public void startManagedSession() {
        this.getSessionManager().startManagedSession();
    }

    public void startManagedSession(boolean autoCommit) {
        this.getSessionManager().startManagedSession(autoCommit);
    }

    public void startManagedSession(Connection connection) {
        this.getSessionManager().startManagedSession(connection);
    }

    public void startManagedSession(TransactionIsolationLevel level) {
        this.getSessionManager().startManagedSession(level);
    }

    public void startManagedSession(ExecutorType execType) {
        this.getSessionManager().startManagedSession(execType);
    }

    public void startManagedSession(ExecutorType execType, boolean autoCommit) {
        this.getSessionManager().startManagedSession(execType, autoCommit);
    }

    public void startManagedSession(
        ExecutorType execType,
        TransactionIsolationLevel level
    ) {
        this.getSessionManager().startManagedSession(execType, level);
    }

    public void startManagedSession(
        ExecutorType execType,
        Connection connection
    ) {
        this.getSessionManager().startManagedSession(execType, connection);
    }

    public boolean isManagedSessionStarted() {
        return this.getSessionManager().isManagedSessionStarted();
    }

    public SqlSession openSession() {
        return this.getSessionManager().openSession();
    }

    public SqlSession openSession(boolean autoCommit) {
        return this.getSessionManager().openSession(autoCommit);
    }

    public SqlSession openSession(Connection connection) {
        return this.getSessionManager().openSession(connection);
    }

    public SqlSession openSession(TransactionIsolationLevel level) {
        return this.getSessionManager().openSession(level);
    }

    public SqlSession openSession(ExecutorType execType) {
        return this.getSessionManager().openSession(execType);
    }

    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return this.getSessionManager().openSession(execType, autoCommit);
    }

    public SqlSession openSession(
        ExecutorType execType,
        TransactionIsolationLevel level
    ) {
        return this.getSessionManager().openSession(execType, level);
    }

    public SqlSession openSession(
        ExecutorType execType,
        Connection connection
    ) {
        return this.getSessionManager().openSession(execType, connection);
    }

    public Configuration getConfiguration() {
        return this.getSessionManager().getConfiguration();
    }

    public <T> T selectOne(String statement) {
        return this.getSessionManager().selectOne(statement);
    }

    public <T> T selectOne(String statement, Object parameter) {
        return this.getSessionManager().selectOne(statement, parameter);
    }

    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.getSessionManager().selectMap(statement, mapKey);
    }

    public <K, V> Map<K, V> selectMap(
        String statement,
        Object parameter,
        String mapKey
    ) {
        return this.getSessionManager().selectMap(statement, parameter, mapKey);
    }

    public <K, V> Map<K, V> selectMap(
        String statement,
        Object parameter,
        String mapKey,
        RowBounds rowBounds
    ) {
        return this.getSessionManager()
            .selectMap(statement, parameter, mapKey, rowBounds);
    }

    public <T> Cursor<T> selectCursor(String statement) {
        return this.getSessionManager().selectCursor(statement);
    }

    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return this.getSessionManager().selectCursor(statement, parameter);
    }

    public <T> Cursor<T> selectCursor(
        String statement,
        Object parameter,
        RowBounds rowBounds
    ) {
        return this.getSessionManager()
            .selectCursor(statement, parameter, rowBounds);
    }

    public <E> List<E> selectList(String statement) {
        return this.getSessionManager().selectList(statement);
    }

    public <E> List<E> selectList(String statement, Object parameter) {
        return this.getSessionManager().selectList(statement, parameter);
    }

    public <E> List<E> selectList(
        String statement,
        Object parameter,
        RowBounds rowBounds
    ) {
        return this.getSessionManager()
            .selectList(statement, parameter, rowBounds);
    }

    public void select(String statement, ResultHandler handler) {
        this.getSessionManager().select(statement, handler);
    }

    public void select(
        String statement,
        Object parameter,
        ResultHandler handler
    ) {
        this.getSessionManager().select(statement, parameter, handler);
    }

    public void select(
        String statement,
        Object parameter,
        RowBounds rowBounds,
        ResultHandler handler
    ) {
        this.getSessionManager()
            .select(statement, parameter, rowBounds, handler);
    }

    public int insert(String statement) {
        return this.getSessionManager().insert(statement);
    }

    public int insert(String statement, Object parameter) {
        return this.getSessionManager().insert(statement, parameter);
    }

    public int update(String statement) {
        return this.getSessionManager().update(statement);
    }

    public int update(String statement, Object parameter) {
        return this.getSessionManager().update(statement, parameter);
    }

    public int delete(String statement) {
        return this.getSessionManager().delete(statement);
    }

    public int delete(String statement, Object parameter) {
        return this.getSessionManager().delete(statement, parameter);
    }

    public <T> T getMapper(Class<T> type) {
        return this.getSessionManager().getMapper(type);
    }

    public <T> T getService(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new MybatisPlusException("Failed to instantiate Service");
        }
    }

    public Connection getConnection() {
        return this.getSessionManager().getConnection();
    }

    public void clearCache() {
        this.getSessionManager().clearCache();
    }

    public void commit() {
        this.getSessionManager().commit();
    }

    public void commit(boolean force) {
        this.getSessionManager().commit(force);
    }

    public void rollback() {
        this.getSessionManager().rollback();
    }

    public void rollback(boolean force) {
        this.getSessionManager().rollback(force);
    }

    public List<BatchResult> flushStatements() {
        return this.getSessionManager().flushStatements();
    }

    public void close() {
        this.getSessionManager().close();
    }
}
