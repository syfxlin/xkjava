package me.ixk.framework.providers;

import java.util.Map;
import javax.sql.DataSource;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

public class DataSourceProvider extends AbstractProvider {

    public DataSourceProvider(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        Map<String, String> config = Config.get("database", Map.class);
        this.app.singleton(
                DataSource.class,
                (container, args) ->
                    new PooledDataSource(
                        config.get("driver"),
                        config.get("url"),
                        config.get("username"),
                        config.get("password")
                    ),
                "dataSource"
            );
    }

    @Override
    public void boot() {
        this.app.make(DataSource.class);
    }
}
