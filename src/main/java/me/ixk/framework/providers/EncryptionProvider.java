package me.ixk.framework.providers;

import me.ixk.framework.facades.Config;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Base64;
import me.ixk.framework.utils.Crypt;

public class EncryptionProvider extends AbstractProvider {

    public EncryptionProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                Crypt.class,
                (container, args) ->
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
