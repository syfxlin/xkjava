/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.database;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MybatisPlus
  implements me.ixk.framework.database.SqlSessionManager {
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

  @Override
  public void startManagedSession() {
    this.getSessionManager().startManagedSession();
  }

  @Override
  public void startManagedSession(boolean autoCommit) {
    this.getSessionManager().startManagedSession(autoCommit);
  }

  @Override
  public void startManagedSession(Connection connection) {
    this.getSessionManager().startManagedSession(connection);
  }

  @Override
  public void startManagedSession(TransactionIsolationLevel level) {
    this.getSessionManager().startManagedSession(level);
  }

  @Override
  public void startManagedSession(ExecutorType execType) {
    this.getSessionManager().startManagedSession(execType);
  }

  @Override
  public void startManagedSession(ExecutorType execType, boolean autoCommit) {
    this.getSessionManager().startManagedSession(execType, autoCommit);
  }

  @Override
  public void startManagedSession(
    ExecutorType execType,
    TransactionIsolationLevel level
  ) {
    this.getSessionManager().startManagedSession(execType, level);
  }

  @Override
  public void startManagedSession(
    ExecutorType execType,
    Connection connection
  ) {
    this.getSessionManager().startManagedSession(execType, connection);
  }

  @Override
  public boolean isManagedSessionStarted() {
    return this.getSessionManager().isManagedSessionStarted();
  }

  @Override
  public SqlSession openSession() {
    return this.getSessionManager().openSession();
  }

  @Override
  public SqlSession openSession(boolean autoCommit) {
    return this.getSessionManager().openSession(autoCommit);
  }

  @Override
  public SqlSession openSession(Connection connection) {
    return this.getSessionManager().openSession(connection);
  }

  @Override
  public SqlSession openSession(TransactionIsolationLevel level) {
    return this.getSessionManager().openSession(level);
  }

  @Override
  public SqlSession openSession(ExecutorType execType) {
    return this.getSessionManager().openSession(execType);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    return this.getSessionManager().openSession(execType, autoCommit);
  }

  @Override
  public SqlSession openSession(
    ExecutorType execType,
    TransactionIsolationLevel level
  ) {
    return this.getSessionManager().openSession(execType, level);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    return this.getSessionManager().openSession(execType, connection);
  }

  @Override
  public Configuration getConfiguration() {
    return this.getSessionManager().getConfiguration();
  }

  @Override
  public <T> T selectOne(String statement) {
    return this.getSessionManager().selectOne(statement);
  }

  @Override
  public <T> T selectOne(String statement, Object parameter) {
    return this.getSessionManager().selectOne(statement, parameter);
  }

  @Override
  public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
    return this.getSessionManager().selectMap(statement, mapKey);
  }

  @Override
  public <K, V> Map<K, V> selectMap(
    String statement,
    Object parameter,
    String mapKey
  ) {
    return this.getSessionManager().selectMap(statement, parameter, mapKey);
  }

  @Override
  public <K, V> Map<K, V> selectMap(
    String statement,
    Object parameter,
    String mapKey,
    RowBounds rowBounds
  ) {
    return this.getSessionManager()
      .selectMap(statement, parameter, mapKey, rowBounds);
  }

  @Override
  public <T> Cursor<T> selectCursor(String statement) {
    return this.getSessionManager().selectCursor(statement);
  }

  @Override
  public <T> Cursor<T> selectCursor(String statement, Object parameter) {
    return this.getSessionManager().selectCursor(statement, parameter);
  }

  @Override
  public <T> Cursor<T> selectCursor(
    String statement,
    Object parameter,
    RowBounds rowBounds
  ) {
    return this.getSessionManager()
      .selectCursor(statement, parameter, rowBounds);
  }

  @Override
  public <E> List<E> selectList(String statement) {
    return this.getSessionManager().selectList(statement);
  }

  @Override
  public <E> List<E> selectList(String statement, Object parameter) {
    return this.getSessionManager().selectList(statement, parameter);
  }

  @Override
  public <E> List<E> selectList(
    String statement,
    Object parameter,
    RowBounds rowBounds
  ) {
    return this.getSessionManager().selectList(statement, parameter, rowBounds);
  }

  @Override
  public void select(String statement, ResultHandler handler) {
    this.getSessionManager().select(statement, handler);
  }

  @Override
  public void select(
    String statement,
    Object parameter,
    ResultHandler handler
  ) {
    this.getSessionManager().select(statement, parameter, handler);
  }

  @Override
  public void select(
    String statement,
    Object parameter,
    RowBounds rowBounds,
    ResultHandler handler
  ) {
    this.getSessionManager().select(statement, parameter, rowBounds, handler);
  }

  @Override
  public int insert(String statement) {
    return this.getSessionManager().insert(statement);
  }

  @Override
  public int insert(String statement, Object parameter) {
    return this.getSessionManager().insert(statement, parameter);
  }

  @Override
  public int update(String statement) {
    return this.getSessionManager().update(statement);
  }

  @Override
  public int update(String statement, Object parameter) {
    return this.getSessionManager().update(statement, parameter);
  }

  @Override
  public int delete(String statement) {
    return this.getSessionManager().delete(statement);
  }

  @Override
  public int delete(String statement, Object parameter) {
    return this.getSessionManager().delete(statement, parameter);
  }

  @Override
  public <T> T getMapper(Class<T> type) {
    return this.getSessionManager().getMapper(type);
  }

  @Override
  public <T> T getService(Class<T> type) {
    return ReflectUtil.newInstance(type);
  }

  @Override
  public Connection getConnection() {
    return this.getSessionManager().getConnection();
  }

  @Override
  public void clearCache() {
    this.getSessionManager().clearCache();
  }

  @Override
  public void commit() {
    this.getSessionManager().commit();
  }

  @Override
  public void commit(boolean force) {
    this.getSessionManager().commit(force);
  }

  @Override
  public void rollback() {
    this.getSessionManager().rollback();
  }

  @Override
  public void rollback(boolean force) {
    this.getSessionManager().rollback(force);
  }

  @Override
  public List<BatchResult> flushStatements() {
    return this.getSessionManager().flushStatements();
  }

  @Override
  public void close() {
    this.getSessionManager().close();
  }
}
