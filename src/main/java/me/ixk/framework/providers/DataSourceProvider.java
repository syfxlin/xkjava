package me.ixk.framework.providers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;

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
                (container, args) -> new HikariDataSource(hikariConfig),
                "dataSource"
            );
    }

    @Override
    public void boot() {
        this.app.make(DataSource.class);
    }
}
