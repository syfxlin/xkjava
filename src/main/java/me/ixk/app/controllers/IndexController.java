/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import me.ixk.app.annotations.Log;
import me.ixk.app.beans.User;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.*;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.Request;
import me.ixk.framework.view.FreeMarker;

@Controller
public class IndexController {
    @Autowired
    public Request request;

    @Autowired("request")
    private HttpServletRequest httpServletRequest;

    private final UsersServiceImpl usersService;

    public IndexController(UsersServiceImpl usersService) {
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
    public String index(Request request, User user, int age) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.request.query("age");
    }

    @GetMapping("/index2")
    @Log
    public String index2(Request request) {
        return this.request.query("age");
    }

    @Middleware(name = "auth")
    @GetMapping("/free-marker")
    public String freeMarker() {
        FreeMarker freeMarker = new FreeMarker();
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name", "FreeMarker");
        return freeMarker.process("index.ftl", map);
    }

    @Autowired
    public void setRequestMethod(Request request) {
        System.out.println(request);
    }

    @ExceptionHandler(value = HttpException.class)
    public String exception() {
        return "controller";
    }
}
