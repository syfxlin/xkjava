/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Value;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:28
 */
@XkJavaTest
class BeanExpressionResolverTest {

    @Autowired
    BeanExpressionResolver resolver;

    @Value("${xkjava.database.username}")
    private String username;

    @Value("${password:password}")
    private String password;

    @Value("${url:https://ixk.me}")
    private String url;

    @Test
    void evaluate() {
        assertTrue(username != null && !username.isEmpty());
        assertEquals("password", password);
        assertEquals("https://ixk.me", url);
        assertEquals(
            resolver.evaluate(
                "#{#e['xkjava.database.username']}",
                String.class
            ),
            "syfxlin"
        );
        assertEquals(
            resolver.evaluate("${xkjava.database.username}", String.class),
            "syfxlin"
        );
        assertEquals(
            resolver.evaluate(
                "${xkjava.database.username} #{'${xkjava.database.username}'}",
                String.class
            ),
            "syfxlin syfxlin"
        );
        assertEquals(
            resolver.evaluate("${#{'xkjava.database.username'}}", String.class),
            "${xkjava.database.username}"
        );
    }
}
