/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.ixk.app.annotations.Log;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User3;
import me.ixk.app.config.TestConfigurationProperties;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Autowired.ProxyType;
import me.ixk.framework.annotation.core.PostConstruct;
import me.ixk.framework.annotation.core.PreDestroy;
import me.ixk.framework.annotation.database.Transactional;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.ExceptionHandler;
import me.ixk.framework.annotation.web.GetMapping;
import me.ixk.framework.annotation.web.Middleware;
import me.ixk.framework.annotation.web.PostMapping;
import me.ixk.framework.exception.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.TextResult;
import me.ixk.framework.http.result.ViewResult;
import me.ixk.framework.ioc.XkJava;

@Controller
public class IndexController {

    @Autowired
    public Request request;

    @Autowired("request")
    private HttpServletRequest httpServletRequest;

    @Autowired(proxyType = ProxyType.DIRECT)
    private Request requestNoProxy;

    private final UsersServiceImpl usersService;

    public IndexController(final UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @PostConstruct
    public void init() {
        System.out.println("Init Controller");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Destroy Controller");
    }

    @Log
    @GetMapping("/controller")
    @Transactional
    public String index(final Request request, final User user, final int age) {
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return this.request.query("age");
    }

    @GetMapping("/index2")
    @Log
    public String index2(final Request request) {
        return this.request.query("age");
    }

    @GetMapping("/user3")
    public User3 user3() {
        return XkJava.of().make(User3.class);
    }

    @GetMapping("/conf")
    public TestConfigurationProperties conf() {
        return XkJava.of().make(TestConfigurationProperties.class);
    }

    @Middleware(name = "auth")
    @GetMapping("/home")
    public ViewResult home() {
        return Result.view("index", Map.of("name", "Home"));
    }

    @GetMapping("/upload")
    public ViewResult upload() {
        return Result.view("upload");
    }

    @PostMapping("/upload")
    public TextResult uploadPost(final Request request) throws IOException {
        return Result.text(request.file("file").string());
    }

    @GetMapping("/exce")
    public TextResult exce() {
        throw new HttpException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/exce-g")
    public TextResult exceG() {
        throw new NullPointerException("message");
    }

    @GetMapping("/encoding")
    public TextResult encoding() {
        return Result.text("中文");
    }

    @Autowired
    public void setRequestMethod(final Request request) {
        System.out.println(request);
    }

    @ExceptionHandler(value = HttpException.class)
    public String exception() {
        return "controller";
    }
}
