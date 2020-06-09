package me.ixk.framework.providers;

import java.util.Map;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Base64;
import me.ixk.framework.utils.JWT;

public class JwtProvider extends AbstractProvider {

    public JwtProvider(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        this.app.singleton(
                JWT.class,
                (container, args) ->
                    new JWT(
                        Base64.decode(Config.get("app.key", String.class)),
                        Config.get("app.hash.algo", String.class),
                        Config.get("app.hash.default_payload", Map.class)
                    ),
                "jwt"
            );
    }

    @Override
    public void boot() {
        this.app.make(JWT.class);
    }
}
