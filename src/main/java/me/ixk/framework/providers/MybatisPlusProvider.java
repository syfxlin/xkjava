package me.ixk.framework.providers;

import java.util.List;
import javax.sql.DataSource;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.MybatisPlus;

public class MybatisPlusProvider extends AbstractProvider {

    public MybatisPlusProvider(Application app) {
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
                MybatisPlus.class,
                (container, args) ->
                    new MybatisPlus(
                        container.make(DataSource.class),
                        mapperPackages
                    ),
                "mybatisPlus"
            );
    }

    @Override
    public void boot() {
        this.app.make(MybatisPlus.class);
    }
}
