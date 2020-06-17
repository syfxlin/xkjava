package me.ixk.app.controllers;

import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
