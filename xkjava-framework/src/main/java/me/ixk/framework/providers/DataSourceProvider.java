/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import static me.ixk.framework.helpers.Facade.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.XkJava;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class DataSourceProvider extends AbstractProvider {

  public DataSourceProvider(XkJava app) {
    super(app);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void register() {
    Map<String, String> config = config().get("database", Map.class);
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDriverClassName(config.get("driver"));
    hikariConfig.setJdbcUrl(config.get("url"));
    hikariConfig.setUsername(config.get("username"));
    hikariConfig.setPassword(config.get("password"));
    this.app.singleton(
        DataSource.class,
        (container, with) -> new HikariDataSource(hikariConfig),
        "dataSource"
      );
  }

  @Override
  public void boot() {
    this.app.make(DataSource.class);
  }
}
