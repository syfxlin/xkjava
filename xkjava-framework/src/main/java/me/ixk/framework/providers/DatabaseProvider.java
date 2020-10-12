/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.registry.attribute.MapperScannerRegistry;

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
        final DataSource dataSource,
        final XkJava app
    ) {
        return new MybatisPlus(
            dataSource,
            app.make(MapperScannerRegistry.class).getScanPackages()
        );
    }
}
