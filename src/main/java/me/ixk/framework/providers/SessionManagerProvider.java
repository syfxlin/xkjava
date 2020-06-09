package me.ixk.framework.providers;

import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.Application;

public class SessionManagerProvider extends AbstractProvider {

    public SessionManagerProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                SessionManager.class,
                SessionManager.class,
                "session"
            );
    }

    @Override
    public void boot() {
        this.app.make(SessionManager.class);
    }
}
