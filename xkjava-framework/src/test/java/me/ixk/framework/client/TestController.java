/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.client;

import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;

/**
 * 测试控制器
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 1:36
 */
@Controller
class TestController {

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
