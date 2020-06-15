package me.ixk.framework.facades;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import me.ixk.framework.utils.MybatisPlus;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;

public class DB extends AbstractFacade {

    protected static MybatisPlus make() {
        return app.make(MybatisPlus.class);
    }

    public void startManagedSession() {
        make().startManagedSession();
    }

    public void startManagedSession(boolean autoCommit) {
        make().startManagedSession(autoCommit);
    }

    public void startManagedSession(Connection connection) {
        make().startManagedSession(connection);
    }

    public void startManagedSession(TransactionIsolationLevel level) {
        make().startManagedSession(level);
    }

    public void startManagedSession(ExecutorType execType) {
        make().startManagedSession(execType);
    }

    public void startManagedSession(ExecutorType execType, boolean autoCommit) {
        make().startManagedSession(execType, autoCommit);
    }

    public void startManagedSession(
        ExecutorType execType,
        TransactionIsolationLevel level
    ) {
        make().startManagedSession(execType, level);
    }

    public void startManagedSession(
        ExecutorType execType,
        Connection connection
    ) {
        make().startManagedSession(execType, connection);
    }

    public boolean isManagedSessionStarted() {
        return make().isManagedSessionStarted();
    }

    public SqlSession openSession() {
        return make().openSession();
    }

    public SqlSession openSession(boolean autoCommit) {
        return make().openSession(autoCommit);
    }

    public SqlSession openSession(Connection connection) {
        return make().openSession(connection);
    }

    public SqlSession openSession(TransactionIsolationLevel level) {
        return make().openSession(level);
    }

    public SqlSession openSession(ExecutorType execType) {
        return make().openSession(execType);
    }

    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return make().openSession(execType, autoCommit);
    }

    public SqlSession openSession(
        ExecutorType execType,
        TransactionIsolationLevel level
    ) {
        return make().openSession(execType, level);
    }

    public SqlSession openSession(
        ExecutorType execType,
        Connection connection
    ) {
        return make().openSession(execType, connection);
    }

    public Configuration getConfiguration() {
        return make().getConfiguration();
    }

    public <T> T selectOne(String statement) {
        return make().selectOne(statement);
    }

    public <T> T selectOne(String statement, Object parameter) {
        return make().selectOne(statement, parameter);
    }

    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return make().selectMap(statement, mapKey);
    }

    public <K, V> Map<K, V> selectMap(
        String statement,
        Object parameter,
        String mapKey
    ) {
        return make().selectMap(statement, parameter, mapKey);
    }

    public <K, V> Map<K, V> selectMap(
        String statement,
        Object parameter,
        String mapKey,
        RowBounds rowBounds
    ) {
        return make().selectMap(statement, parameter, mapKey, rowBounds);
    }

    public <T> Cursor<T> selectCursor(String statement) {
        return make().selectCursor(statement);
    }

    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return make().selectCursor(statement, parameter);
    }

    public <T> Cursor<T> selectCursor(
        String statement,
        Object parameter,
        RowBounds rowBounds
    ) {
        return make().selectCursor(statement, parameter, rowBounds);
    }

    public <E> List<E> selectList(String statement) {
        return make().selectList(statement);
    }

    public <E> List<E> selectList(String statement, Object parameter) {
        return make().selectList(statement, parameter);
    }

    public <E> List<E> selectList(
        String statement,
        Object parameter,
        RowBounds rowBounds
    ) {
        return make().selectList(statement, parameter, rowBounds);
    }

    @SuppressWarnings("rawtypes")
    public void select(String statement, ResultHandler handler) {
        make().select(statement, handler);
    }

    @SuppressWarnings("rawtypes")
    public void select(
        String statement,
        Object parameter,
        ResultHandler handler
    ) {
        make().select(statement, parameter, handler);
    }

    @SuppressWarnings("rawtypes")
    public void select(
        String statement,
        Object parameter,
        RowBounds rowBounds,
        ResultHandler handler
    ) {
        make().select(statement, parameter, rowBounds, handler);
    }

    public int insert(String statement) {
        return make().insert(statement);
    }

    public int insert(String statement, Object parameter) {
        return make().insert(statement, parameter);
    }

    public int update(String statement) {
        return make().update(statement);
    }

    public int update(String statement, Object parameter) {
        return make().update(statement, parameter);
    }

    public int delete(String statement) {
        return make().delete(statement);
    }

    public int delete(String statement, Object parameter) {
        return make().delete(statement, parameter);
    }

    public <T> T getMapper(Class<T> type) {
        return make().getMapper(type);
    }

    public <T> T getService(Class<T> type) {
        return make().getService(type);
    }

    public Connection getConnection() {
        return make().getConnection();
    }

    public void clearCache() {
        make().clearCache();
    }

    public void commit() {
        make().commit();
    }

    public void commit(boolean force) {
        make().commit(force);
    }

    public void rollback() {
        make().rollback();
    }

    public void rollback(boolean force) {
        make().rollback(force);
    }

    public List<BatchResult> flushStatements() {
        return make().flushStatements();
    }

    public void close() {
        make().close();
    }
}
