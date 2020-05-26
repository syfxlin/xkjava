package me.ixk.bootstrap;

import me.ixk.facades.AbstractFacade;
import me.ixk.ioc.Application;

public class RegisterFacades extends AbstractBootstrap {

    public RegisterFacades(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        AbstractFacade.setApplication(app);
    }
}
