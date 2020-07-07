/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Base64;
import me.ixk.framework.utils.Crypt;

import static me.ixk.framework.helpers.Facade.config;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 6)
public class EncryptionProvider extends AbstractProvider {

    public EncryptionProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                Crypt.class,
                (container, with) ->
                    new Crypt(
                        Base64.decode(config().get("app.key", String.class)),
                        config().get("app.cipher", String.class)
                    ),
                "crypt"
            );
    }

    @Override
    public void boot() {
        this.app.make(Crypt.class);
    }
}
