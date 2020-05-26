package me.ixk;

import me.ixk.bootstrap.LoadConfiguration;
import me.ixk.bootstrap.LoadEnvironmentVariables;
import me.ixk.facades.AbstractFacade;
import me.ixk.facades.Config;
import me.ixk.ioc.Application;

public class App {

    public static void main(String[] args) {
        Application application = Application.boot();
        LoadEnvironmentVariables loadEnvironmentVariables = new LoadEnvironmentVariables(
            application
        );
        loadEnvironmentVariables.boot();
        LoadConfiguration loadConfiguration = new LoadConfiguration(
            application
        );
        loadConfiguration.boot();
        AbstractFacade.setApplication(application);
        Config.all();
    }
}
