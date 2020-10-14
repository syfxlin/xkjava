/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import java.util.Map;

/**
 * 合并后的路由数据
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:49
 */
public class MergeRouteData {
    protected String regex;

    protected Map<Integer, RouteData> routeMap;

    public MergeRouteData(String regex, Map<Integer, RouteData> routeMap) {
        this.regex = regex;
        this.routeMap = routeMap;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Map<Integer, RouteData> getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(Map<Integer, RouteData> routeMap) {
        this.routeMap = routeMap;
    }
}
