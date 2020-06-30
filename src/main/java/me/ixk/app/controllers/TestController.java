package me.ixk.app.controllers;

import me.ixk.app.beans.User;
import me.ixk.framework.annotations.Autowired;
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
    public String post(User user) {
        return "post";
    }

    @Autowired
    public void user(User user) {
        System.out.println(user);
    }
}
