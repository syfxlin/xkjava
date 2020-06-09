package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.annotations.Log;
import me.ixk.app.middleware.Middleware2;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.facades.View;
import me.ixk.framework.http.Request;

@Controller
public class IndexController {

    @PostMapping("/controller")
    @Middleware(middleware = Middleware2.class)
    @Log
    public String index(Request request) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("text", "String");
        return View.make("index", map).render();
    }
}
