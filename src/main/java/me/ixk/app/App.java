package me.ixk.app;

import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.ioc.Application;

@ComponentScan(basePackages = { "me.ixk.app" })
public class App {

    public static void main(String[] args) {
        Application.create().boot(App.class, args);
    }
}
