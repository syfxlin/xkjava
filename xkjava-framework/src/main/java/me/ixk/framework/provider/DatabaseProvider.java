/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.provider;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import me.ixk.framework.annotation.Bean;
import me.ixk.framework.annotation.ConditionalOnMissingBean;
import me.ixk.framework.annotation.Provider;
import me.ixk.framework.config.DatabaseProperties;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
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
    public DataSource dataSource(final DatabaseProperties properties) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(properties.getDriver());
        hikariConfig.setJdbcUrl(properties.getUrl());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setPassword(properties.getPassword());
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
        final Configuration configuration = new MybatisConfiguration(
            environment
        );
        final List<String> scanPackages = app
            .make(MapperScannerRegistry.class)
            .getScanPackages();
        if (scanPackages != null) {
            for (final String scanPackage : scanPackages) {
                configuration.addMappers(scanPackage);
            }
        }
        configuration.addInterceptor(new MybatisPlusInterceptor());
        return configuration;
    }
}
