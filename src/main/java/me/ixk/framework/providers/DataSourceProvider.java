package me.ixk.framework.providers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;

import javax.sql.DataSource;
import java.util.Map;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class DataSourceProvider extends AbstractProvider {

    public DataSourceProvider(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        Map<String, String> config = Config.get("database", Map.class);
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
