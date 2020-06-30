package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Base64;
import me.ixk.framework.utils.Crypt;

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
                        Base64.decode(Config.get("app.key", String.class)),
                        Config.get("app.cipher", String.class)
                    ),
                "crypt"
            );
    }

    @Override
    public void boot() {
        this.app.make(Crypt.class);
    }
}
