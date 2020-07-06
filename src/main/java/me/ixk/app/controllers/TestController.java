/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import me.ixk.app.beans.User;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.PostMapping;

@Controller
public class TestController {

    @GetMapping("/test/{id}")
    public String test(int id) {
        return "test";
    }

    @PostMapping("/post")
    public String post(User user) {
        return "post";
    }
}
