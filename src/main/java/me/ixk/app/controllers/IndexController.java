package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import me.ixk.app.annotations.Log;
import me.ixk.app.beans.User;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.*;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.facades.View;
import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.Application;

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
        //        Auth.register(registerUser);
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("text", "String");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return View.make("index", map).render();
    }

    @Middleware("guest")
    @GetMapping("/login")
    public me.ixk.framework.view.View login(Request request) {
        //        return View.make("login");
        throw new Exception();
    }

    @Bean(name = "user")
    public User getUser() {
        return new User("syfxlin", 20);
    }

    @Autowired
    public void setApplication(Application application) {
        this.getUser();
    }

    @ExceptionHandler(value = HttpException.class)
    public String exception() {
        return "controller";
    }
}
