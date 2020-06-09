package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.annotations.Log;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.middleware.Middleware2;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.facades.App;
import me.ixk.framework.facades.View;
import me.ixk.framework.http.Request;
import me.ixk.framework.kernel.Auth;

@Controller
public class IndexController {
    @Autowired
    public UsersServiceImpl usersService;

    public IndexController(UsersServiceImpl usersService) {
        System.out.println();
    }

    @PostMapping("/controller")
    @Middleware(middleware = Middleware2.class)
    @Log
    public String index(Request request) {
        RegisterUser user = new RegisterUser();
        user.setUsername("syfxlin1");
        user.setPassword("password");
        user.setEmail("syfxlin@gmail.com");
        user.setPasswordConfirmed("password");
        user.setNickname("nickname");
        App.make(Auth.class).register(user);
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("text", "String");
        return View.make("index", map).render();
    }
}
