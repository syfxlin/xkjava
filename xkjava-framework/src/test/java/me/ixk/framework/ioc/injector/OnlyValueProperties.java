/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.core.Value;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 1:31
 */
@Component
public class OnlyValueProperties {

    @Value("${xkjava.database.url}")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
