package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.annotations.Log;
import me.ixk.app.beans.User;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.*;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.facades.View;
import me.ixk.framework.http.Request;

@Controller
public class IndexController {
    @Autowired
    public UsersServiceImpl usersService;

    public IndexController(UsersServiceImpl usersService) {
        //
    }

    @Log
    @GetMapping("/controller")
    @Transactional
    public String index(Request request, User user, int age) {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setUsername("syfxlin2");
        registerUser.setNickname("nickname");
        registerUser.setEmail("syfxlin@gmail.com");
        registerUser.setPassword("123456");
        registerUser.setPasswordConfirmed("123456");
        Auth.register(registerUser);
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("text", "String");
        return View.make("index", map).render();
    }

    @Middleware("guest")
    @GetMapping("/login")
    public me.ixk.framework.view.View login() {
        return View.make("login");
    }
}
