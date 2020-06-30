package me.ixk.app.controllers;

import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.PostMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/post")
    public String post() {
        return "post";
    }
}
