package me.ixk.framework.facades;

import me.ixk.app.entity.LoginUser;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.entity.Users;

public class Auth extends AbstractFacade {

    protected static me.ixk.framework.kernel.Auth make() {
        return app.make(me.ixk.framework.kernel.Auth.class);
    }

    public static me.ixk.framework.kernel.Auth.Result register(
        RegisterUser user
    ) {
        return make().register(user);
    }

    public static me.ixk.framework.kernel.Auth.Result login(LoginUser user) {
        return make().login(user);
    }

    public static me.ixk.framework.kernel.Auth.Result attempt(LoginUser user) {
        return make().attempt(user);
    }

    public static void logout() {
        make().logout();
    }

    public static Users user() {
        return make().user();
    }

    public static boolean check() {
        return make().check();
    }

    public static boolean guest() {
        return make().guest();
    }

    public static boolean viaRemember() {
        return make().viaRemember();
    }
}
