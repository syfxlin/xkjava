package me.ixk.framework.bootstrap;

import me.ixk.framework.facades.AbstractFacade;
import me.ixk.framework.ioc.Application;

public class RegisterFacades extends AbstractBootstrap {

    public RegisterFacades(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        AbstractFacade.setApplication(app);
    }
}
