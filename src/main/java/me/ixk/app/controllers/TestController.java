/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.framework.annotations.*;
import me.ixk.framework.http.WebDataBinder;
import me.ixk.framework.http.converter.TestConverter;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired(value = "name", required = false)
    private String name;

    @GetMapping("/{id}")
    public String test(int id) {
        return "test";
    }

    @PostMapping("/post")
    public String post(
        User user,
        @DataBind(name = "user") User user1,
        @DataBind(name = "user3", required = false) String user2
    ) {
        return "post";
    }

    @PostMapping("/body")
    public String body(
        @DataBind(name = "&body") JsonNode body,
        @DataBind User2 user2
    ) {
        return "body";
    }

    @InitBinder
    public void binder(WebDataBinder binder) {
        User2 user2 = new User2();
        user2.setName("user3");
        user2.setAge(17);
        binder.addDefault("user3", user2);
        binder.addDefault("user4.name", "user4");
        binder.addConverter("user4.name", new TestConverter());
    }
}
