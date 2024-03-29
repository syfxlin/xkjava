/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;

/**
 * ServiceImpl
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@SuppressWarnings("ALL")
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    protected final Log log = LogFactory.getLog(getClass());

    protected SqlSessionManager sqlSessionManager = XkJava
        .of()
        .make(SqlSessionManager.class);

    protected final Class<M> mapperClass = currentMapperClass();
    protected final Class<?> entityClass = currentModelClass();

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     * @deprecated 3.3.1
     */
    @Deprecated
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<M> currentMapperClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(entityClass);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(
            sqlSession,
            GlobalConfigUtils.currentSessionFactory(entityClass)
        );
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper
            .table(entityClass)
            .getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(
            entityList,
            batchSize,
            (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity)
        );
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(
            tableInfo,
            "error: can not execute. because can not find cache of TableInfo for entity!"
        );
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(
            keyProperty,
            "error: can not execute. because can not find column for id from entity!"
        );
        return executeBatch(
            entityList,
            batchSize,
            (sqlSession, entity) -> {
                Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
                if (
                    StringUtils.checkValNull(idVal) ||
                    Objects.isNull(getById((Serializable) idVal))
                ) {
                    sqlSession.insert(
                        tableInfo.getSqlStatement(
                            SqlMethod.INSERT_ONE.getMethod()
                        ),
                        entity
                    );
                } else {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(
                        tableInfo.getSqlStatement(
                            SqlMethod.UPDATE_BY_ID.getMethod()
                        ),
                        param
                    );
                }
            }
        );
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(
            entityList,
            batchSize,
            (sqlSession, entity) -> {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                sqlSession.update(sqlStatement, param);
            }
        );
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(
                tableInfo,
                "error: can not execute. because can not find cache of TableInfo for entity!"
            );
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(
                keyProperty,
                "error: can not execute. because can not find column for id from entity!"
            );
            Object idVal = ReflectionKit.getFieldValue(
                entity,
                tableInfo.getKeyProperty()
            );
            return (
                    StringUtils.checkValNull(idVal) ||
                    Objects.isNull(getById((Serializable) idVal))
                )
                ? save(entity)
                : updateById(entity);
        }
        return false;
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        if (throwEx) {
            return this.getBaseMapper().selectOne(queryWrapper);
        }
        return SqlHelper.getObject(
            log,
            this.getBaseMapper().selectList(queryWrapper)
        );
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(
            log,
            this.getBaseMapper().selectMaps(queryWrapper)
        );
    }

    @Override
    public <V> V getObj(
        Wrapper<T> queryWrapper,
        Function<? super Object, V> mapper
    ) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    @Override
    public M getBaseMapper() {
        return sqlSessionManager.getMapper(mapperClass);
    }

    /**
     * 执行批量操作
     *
     * @param consumer consumer
     * @since 3.3.0
     * @deprecated 3.3.1 后面我打算移除掉 {@link #executeBatch(Collection, int, BiConsumer)} }.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(
            entityClass
        );
        SqlSession sqlSession = sqlSessionFactory.openSession(
            ExecutorType.BATCH
        );
        try {
            consumer.accept(sqlSession);
            //非事物情况下，强制commit。
            sqlSession.commit();
            return true;
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            throw ExceptionUtils.mpe(unwrapped);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(
        Collection<E> list,
        int batchSize,
        BiConsumer<SqlSession, E> consumer
    ) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return (
            !CollectionUtils.isEmpty(list) &&
            executeBatch(
                sqlSession -> {
                    int size = list.size();
                    int i = 1;
                    for (E element : list) {
                        consumer.accept(sqlSession, element);
                        if ((i % batchSize == 0) || i == size) {
                            sqlSession.flushStatements();
                        }
                        i++;
                    }
                }
            )
        );
    }

    /**
     * 执行批量操作（默认批次提交数量{@link IService#DEFAULT_BATCH_SIZE}）
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @param <E>      泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(
        Collection<E> list,
        BiConsumer<SqlSession, E> consumer
    ) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }
}
