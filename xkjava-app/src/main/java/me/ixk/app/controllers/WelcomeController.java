/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import me.ixk.framework.annotation.Controller;
import me.ixk.framework.annotation.GetMapping;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.TextResult;

@Controller
public class WelcomeController {

    // @Middleware(name = "auth")
    @GetMapping("/welcome")
    public TextResult welcome() {
        return Result.text("welcome");
    }
}
