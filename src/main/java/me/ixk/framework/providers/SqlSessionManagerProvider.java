/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.database.MybatisPlus;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;

import javax.sql.DataSource;
import java.util.List;

import static me.ixk.framework.helpers.Facade.config;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class SqlSessionManagerProvider extends AbstractProvider {

    public SqlSessionManagerProvider(XkJava app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        List<String> mapperPackages = config()
            .get("database.mapper_packages", List.class);
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
