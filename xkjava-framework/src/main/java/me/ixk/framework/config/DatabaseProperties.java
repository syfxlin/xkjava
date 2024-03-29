/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import me.ixk.framework.annotation.core.ConfigurationProperties;

/**
 * 数据库配置文件
 *
 * @author Otstar Lin
 * @date 2020/11/5 下午 9:51
 */
@ConfigurationProperties(prefix = "xkjava.database")
public class DatabaseProperties {

    private String driver;
    private String url;
    private String username;
    private String password;

    public String getDriver() {
        return driver;
    }

    public void setDriver(final String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
