package me.ixk.app.beans;

import me.ixk.framework.annotations.RequestMapping;

public class Controller {

    @RequestMapping("/controller")
    public void index() {
        System.out.println("controller");
    }
}
