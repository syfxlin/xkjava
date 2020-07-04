/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Base64;
import me.ixk.framework.utils.JWT;

import java.util.Map;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 8)
public class JwtProvider extends AbstractProvider {

    public JwtProvider(Application app) {
        super(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        this.app.singleton(
                JWT.class,
                (container, with) ->
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
