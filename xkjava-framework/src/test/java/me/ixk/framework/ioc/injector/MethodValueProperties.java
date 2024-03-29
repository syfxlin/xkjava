/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.core.Value;

/**
 * @author Otstar Lin
 * @date 2020/12/26 下午 10:07
 */
@Component
public class MethodValueProperties {

    private String value;

    public String getValue() {
        return value;
    }

    @Autowired
    public void setValueWithValue(
        @Value("${xkjava.database.url}") final String value
    ) {
        this.value = value;
    }
}
