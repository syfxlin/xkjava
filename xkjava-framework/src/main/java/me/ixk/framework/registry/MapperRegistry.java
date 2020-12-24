/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.factory.FactoryBean;
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
        String scopeType,
        MergedAnnotation annotation
    ) {
        return app.bind(
            new FactoryBean<>() {
                @Override
                public Object getObject() throws Exception {
                    return app
                        .make(SqlSessionManager.class)
                        .getMapper((Class<?>) element);
                }

                @Override
                public Class<?> getObjectType() {
                    return (Class<?>) element;
                }
            },
            scopeType
        );
    }
}
