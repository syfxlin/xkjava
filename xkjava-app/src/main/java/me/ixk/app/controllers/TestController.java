/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.BodyValue;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.HeaderValue;
import me.ixk.framework.annotations.InitBinder;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.annotations.QueryValue;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.ResponseStatus;
import me.ixk.framework.annotations.VerifyCsrf;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;
import me.ixk.framework.web.WebDataBinder;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired(value = "name", required = false)
    private String name;

    @GetMapping("/{id}")
    public String test(int id) {
        return "test";
    }

    @GetMapping("/get-a")
    public int getAnnotation(@QueryValue int id) {
        return id;
    }

    @PostMapping("/post-a")
    public int postAnnotation(@BodyValue int id) {
        return id;
    }

    @CrossOrigin
    @GetMapping("/header-a")
    public String headerAnnotation(@HeaderValue String host) {
        return host;
    }

    @PostMapping("/post")
    @ResponseStatus
    @VerifyCsrf
    public String post(
        User user,
        @DataBind(name = "user") User user2,
        @DataBind(name = "user3") User2 user3,
        @Valid @DataBind(name = "user4") User2 user4,
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
    }

    @PreDestroy
    public void destroy() {
        System.out.println("TestController destroy");
    }
}
