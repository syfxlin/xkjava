package me.ixk;

import me.ixk.facades.Config;
import me.ixk.ioc.Application;

public class App {

    public static void main(String[] args) {
        Application application = Application.createAndBoot();
        Config.get("app.provider", "123");
    }
}
