/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.EnvValue;

/**
 * 框架主要的一些设置
 *
 * @author Otstar Lin
 * @date 2020/11/8 下午 9:19
 */
@ConfigurationProperties(prefix = "xkjava.app")
public class AppProperties {
    @EnvValue(defaultValue = "8080")
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
