package me.ixk.app.controllers;

import me.ixk.app.middleware.Middleware2;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.http.Request;

public class Controller {

    @PostMapping("/controller")
    @Middleware(middleware = Middleware2.class)
    public void index(Request request) {
        System.out.println("controller");
    }
}
