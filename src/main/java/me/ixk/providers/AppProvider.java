package me.ixk.providers;

import me.ixk.ioc.Application;

public class AppProvider extends AbstractProvider {

    public AppProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        //
        System.out.println("register");
    }

    @Override
    public void boot() {
        //
        System.out.println("boot");
    }
}
