/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.registry.after.MapperScannerRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * 数据库提供者
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:52
 */
@Provider
public class DatabaseProvider {

    @Bean(name = "dataSource")
    @ConditionalOnMissingBean(name = "dataSource", value = DataSource.class)
    public DataSource dataSource(final Environment env) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(env.get("database.driver"));
        hikariConfig.setJdbcUrl(env.get("database.url"));
        hikariConfig.setUsername(env.get("database.username"));
        hikariConfig.setPassword(env.get("database.password"));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "sqlSessionManager")
    @ConditionalOnMissingBean(
        name = "sqlSessionManager",
        value = SqlSessionManager.class
    )
    public SqlSessionManager sqlSessionManager(
        final SqlSessionFactoryBuilder builder,
        final Configuration configuration
    ) {
        return new MybatisPlus(
            org.apache.ibatis.session.SqlSessionManager.newInstance(
                builder.build(configuration)
            )
        );
    }

    @Bean(name = "sqlSessionFactoryBuilder")
    @ConditionalOnMissingBean(
        name = "sqlSessionFactoryBuilder",
        value = SqlSessionFactoryBuilder.class
    )
    public SqlSessionFactoryBuilder sqlSessionFactoryBuilder() {
        return new SqlSessionFactoryBuilder();
    }

    @Bean(name = "databaseConfiguration")
    @ConditionalOnMissingBean(
        name = "databaseConfiguration",
        value = Configuration.class
    )
    public Configuration databaseConfiguration(
        final DataSource dataSource,
        final XkJava app
    ) {
        final org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment(
            "database",
            new JdbcTransactionFactory(),
            dataSource
        );
        Configuration configuration = new MybatisConfiguration(environment);
        final List<String> scanPackages = app
            .make(MapperScannerRegistry.class)
            .getScanPackages();
        if (scanPackages != null) {
            for (String scanPackage : scanPackages) {
                configuration.addMappers(scanPackage);
            }
        }
        configuration.addInterceptor(new PaginationInterceptor());
        configuration.addInterceptor(new OptimisticLockerInterceptor());
        configuration.addInterceptor(new SqlExplainInterceptor());
        return configuration;
    }
}
