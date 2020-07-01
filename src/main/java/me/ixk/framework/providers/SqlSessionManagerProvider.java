package me.ixk.framework.providers;

import java.util.List;
import javax.sql.DataSource;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class SqlSessionManagerProvider extends AbstractProvider {

    public SqlSessionManagerProvider(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        List<String> mapperPackages = Config.get(
            "database.mapper_packages",
            List.class
        );
        this.app.singleton(
                SqlSessionManager.class,
                (container, with) ->
                    new MybatisPlus(
                        container.make(DataSource.class),
                        mapperPackages
                    ),
                "sqlSessionManager"
            );
    }

    @Override
    public void boot() {
        this.app.make(SqlSessionManager.class);
    }
}
