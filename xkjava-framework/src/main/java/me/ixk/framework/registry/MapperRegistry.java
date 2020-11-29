/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * MapperRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:07
 */
public class MapperRegistry implements BeanBindRegistry {

    @Override
    public Binding register(
        XkJava app,
        AnnotatedElement element,
        ScopeType scopeType,
        MergedAnnotation annotation
    ) {
        return app.bind(
            (Class<?>) element,
            (container, with) ->
                container
                    .make(SqlSessionManager.class)
                    .getMapper((Class<?>) element),
            null,
            scopeType
        );
    }
}
