/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import static me.ixk.framework.helpers.Facade.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Bean.BindType;
import me.ixk.framework.annotations.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Bean(name = "dataSource", bindType = BindType.BIND)
    @SuppressWarnings("unchecked")
    public DataSource dataSource() {
        final Map<String, String> config = config().get("database", Map.class);
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(config.get("driver"));
        hikariConfig.setJdbcUrl(config.get("url"));
        hikariConfig.setUsername(config.get("username"));
        hikariConfig.setPassword(config.get("password"));
        return new HikariDataSource(hikariConfig);
    }
}
