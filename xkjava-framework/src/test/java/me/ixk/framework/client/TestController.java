/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.client;

import com.fasterxml.jackson.databind.JsonNode;
import me.ixk.framework.annotation.BodyValue;
import me.ixk.framework.annotation.Controller;
import me.ixk.framework.annotation.GetMapping;
import me.ixk.framework.annotation.HeaderValue;
import me.ixk.framework.annotation.PostMapping;

/**
 * 测试控制器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 1:36
 */
@Controller
class TestController {

    @GetMapping("/welcome")
    public String welcome(
        @HeaderValue(name = "Test", required = false) final String test
    ) {
        return test == null ? "welcome" : test;
    }

    @PostMapping("/post")
    public JsonNode post(@BodyValue JsonNode node) {
        return node;
    }

    @GetMapping("/get")
    public String get(String key) {
        return key;
    }
}
