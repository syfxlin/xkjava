/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.annotation.ConfigurationProperties;
import me.ixk.framework.annotation.PropertyValue;

/**
 * 框架主要的一些设置
 *
 * @author Otstar Lin
 * @date 2020/11/8 下午 9:19
 */
@ConfigurationProperties(prefix = "xkjava.app")
public class AppProperties {

    @PropertyValue(defaultValue = "8080")
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }
}
