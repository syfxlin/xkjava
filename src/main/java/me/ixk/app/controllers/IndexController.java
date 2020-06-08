package me.ixk.app.controllers;

import me.ixk.app.annotations.Log;
import me.ixk.app.middleware.Middleware2;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.http.Request;

@Controller
public class IndexController {

    @PostMapping("/controller")
    @Middleware(middleware = Middleware2.class)
    @Log
    public void index(Request request) {
        System.out.println("controller");
    }
}
