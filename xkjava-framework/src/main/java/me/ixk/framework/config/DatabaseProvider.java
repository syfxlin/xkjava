/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.kernel.Config;

@Configuration
public class DatabaseProvider {

    @Bean(name = "dataSource")
    @ConditionalOnMissingBean(name = "dataSource", value = DataSource.class)
    @SuppressWarnings("unchecked")
    public DataSource dataSource(final Config config) {
        final Map<String, String> item = config.get("database", Map.class);
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(item.get("driver"));
        hikariConfig.setJdbcUrl(item.get("url"));
        hikariConfig.setUsername(item.get("username"));
        hikariConfig.setPassword(item.get("password"));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "sqlSessionManager")
    @ConditionalOnMissingBean(
        name = "sqlSessionManager",
        value = SqlSessionManager.class
    )
    @SuppressWarnings("unchecked")
    public SqlSessionManager sqlSessionManager(
        final DataSource dataSource,
        final Config config
    ) {
        return new MybatisPlus(
            dataSource,
            config.get("database.mapper_packages", List.class)
        );
    }
}
