/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers.Auth;

import static me.ixk.framework.helper.Facade.response;

import me.ixk.app.auth.Auth;
import me.ixk.app.entity.RegisterUser;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.GetMapping;
import me.ixk.framework.annotation.web.Middleware;
import me.ixk.framework.annotation.web.PostMapping;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;
import me.ixk.framework.ioc.XkJava;

@Controller
public class RegisterController {

    @GetMapping("/register")
    @Middleware(name = "guest")
    public ViewResult index() {
        return Result.view("auth/register");
    }

    @PostMapping("/register")
    @Middleware(name = "guest")
    public Response register(final RegisterUser user) {
        final Auth.Result result = XkJava.of().make(Auth.class).register(user);
        if (result.isOk()) {
            return response().redirect("/login");
        }
        return response().text(result.getErrors().toString());
    }
}
