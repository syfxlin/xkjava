/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.route;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/12 下午 10:07
 */
class RouteParserTest {

    @Test
    void parse() {
        final RouteParser parser = new RouteParser();

        final RouteData d1 = parser.parse("/user");
        assertEquals("/user", d1.getRegex());

        final RouteData d2 = parser.parse("/user/{id}");
        assertEquals("/user/([^/]+)", d2.getRegex());

        final RouteData d3 = parser.parse("/user/{id?}");
        assertEquals("/user/?([^/]+)?", d3.getRegex());

        final RouteData d4 = parser.parse("/user/{id:[a-z]+}");
        assertEquals("/user/([a-z]+)", d4.getRegex());

        final RouteData d5 = parser.parse("/user/{id?:[a-z]+}");
        assertEquals("/user/?([a-z]+)?", d5.getRegex());

        final RouteData d6 = parser.parse("/user/{id}/{name}");
        assertEquals("/user/([^/]+)/([^/]+)", d6.getRegex());

        final RouteData d7 = parser.parse("/user/name");
        assertEquals("/user/name", d7.getRegex());

        final RouteData d8 = parser.parse("/user/name/{name}");
        assertEquals("/user/name/([^/]+)", d8.getRegex());
    }
}
