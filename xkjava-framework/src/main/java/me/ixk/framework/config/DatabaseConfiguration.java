/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Bean.BindType;
import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.kernel.Config;

@Configuration
public class DatabaseConfiguration {

    @Bean(name = "dataSource", bindType = BindType.BIND)
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
}
