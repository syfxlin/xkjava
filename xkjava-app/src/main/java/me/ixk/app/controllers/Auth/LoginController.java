/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers.Auth;

import static me.ixk.framework.helpers.Facade.response;

import me.ixk.app.auth.Auth;
import me.ixk.app.entity.LoginUser;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.Middleware;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.result.RedirectResult;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;
import me.ixk.framework.ioc.XkJava;

@Controller
public class LoginController {

    @GetMapping("/login")
    @Middleware(name = "guest")
    public ViewResult index() {
        return Result.view("auth/login");
    }

    @PostMapping("/login")
    @Middleware(name = "guest")
    public Response login(final LoginUser user) {
        final Auth.Result result = XkJava.of().make(Auth.class).login(user);
        if (result.isOk()) {
            return response().redirect("/home");
        }
        return response().text(result.getErrors().toString());
    }

    @GetMapping("/logout")
    @Middleware(name = "auth")
    public RedirectResult logout() {
        XkJava.of().make(Auth.class).logout();
        return Result.redirect("/login");
    }
}
