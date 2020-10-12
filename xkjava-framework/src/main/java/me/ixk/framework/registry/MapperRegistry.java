/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public class MapperRegistry implements ImportBeanRegistry {

    @Override
    public Binding register(
        XkJava app,
        Class<?> clazz,
        ScopeType scopeType,
        MergedAnnotation annotation
    ) {
        return app.bind(
            clazz,
            (container, with) ->
                container.make(SqlSessionManager.class).getMapper(clazz),
            null,
            scopeType
        );
    }
}
