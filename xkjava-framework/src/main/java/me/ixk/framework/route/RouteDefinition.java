/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

@SuppressWarnings("EmptyMethod")
@FunctionalInterface
public interface RouteDefinition {
    void routes(RouteCollector routeCollector);
}
