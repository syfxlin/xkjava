package me.ixk.app.controllers;

import javax.servlet.http.HttpServletRequest;
import me.ixk.app.annotations.Log;
import me.ixk.app.beans.User;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.annotations.*;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.Request;

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

    @PostConstruct
    public void init() {
        System.out.println("Init Controller");
    }

    @Log
    @GetMapping("/controller")
    @Transactional
    public String index(Request request, User user, int age) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.request.query("age");
    }

    @Middleware("guest")
    @GetMapping("/login")
    @Log
    public String login(Request request) {
        return this.request.query("age");
    }

    @ExceptionHandler(value = HttpException.class)
    public String exception() {
        return "controller";
    }
}
