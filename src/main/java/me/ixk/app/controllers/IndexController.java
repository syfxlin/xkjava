package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.facades.Cookie;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.SetCookie;

@Controller
public class IndexController {
    @Autowired
    public UsersServiceImpl usersService;

    public IndexController(UsersServiceImpl usersService) {
        //
    }

    @GetMapping("/controller")
    public String index(Request request) {
        Cookie.forever(new SetCookie("key", "value"));

        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("text", "String");
        //        return View.make("index", map).render();
        throw new RuntimeException("Error");
    }
}
