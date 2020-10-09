/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public class MapperRegistrar implements ImportBeanRegistrar {

    @Override
    public void register(
        XkJava app,
        Class<?> element,
        ScopeType scopeType,
        MergeAnnotation annotation
    ) {
        app.bind(
            element,
            (container, with) ->
                container.make(SqlSessionManager.class).getMapper(element),
            null,
            scopeType
        );
    }
}
