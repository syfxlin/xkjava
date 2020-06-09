package me.ixk.framework.providers;

import me.ixk.framework.http.CookieManager;
import me.ixk.framework.ioc.Application;

public class CookieManagerProvider extends AbstractProvider {

    public CookieManagerProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(CookieManager.class, CookieManager.class, "cookie");
    }

    @Override
    public void boot() {
        this.app.make(CookieManager.class);
    }
}
