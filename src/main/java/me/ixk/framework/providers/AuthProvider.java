package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Auth;

public class AuthProvider extends AbstractProvider {

    public AuthProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(Auth.class, Auth.class, "auth");
    }

    @Override
    public void boot() {
        this.app.make(Auth.class);
    }
}
