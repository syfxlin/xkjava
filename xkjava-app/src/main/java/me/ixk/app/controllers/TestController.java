/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.app.converter.TestConverter;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.InitBinder;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.http.WebDataBinder;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;

@Controller
@Bean(destroyMethod = "destroy")
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
        @Valid @DataBind(name = "user1") User2 user1,
        @Valid @DataBind(name = "user2") User2 user2,
        // 如果不传入这两个其中一个参数，则会抛出异常
        ValidGroup validGroup,
        ValidResult<User2> validResult
    ) {
        return "post";
    }

    @PostMapping("/body")
    public String body(
        @DataBind(name = "&body") JsonNode body,
        @DataBind User2 user2,
        @DataBind(name = "request") HttpServletRequest request
    ) {
        return "body";
    }

    @GetMapping("/case")
    public String getCase(String userName) {
        return userName;
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

    public void destroy() {
        System.out.println("TestController destroy");
    }
}
