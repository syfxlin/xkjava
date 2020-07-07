/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.framework.annotations.*;
import me.ixk.framework.http.WebDataBinder;

@Controller
public class TestController {

    @GetMapping("/test/{id}")
    public String test(int id) {
        return "test";
    }

    @PostMapping("/post")
    public String post(
        User user,
        @DataBind(name = "user") User user1,
        @DataBind(name = "user3") User2 user2,
        @DataBind(name = "user4") User2 user3
    ) {
        return "post";
    }

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.addDefault("user3", new User2("user3", 17));
        binder.addDefault("user4.name", "user4");
    }
}
