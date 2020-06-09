package me.ixk.framework.providers;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Hash;

public class HashProvider extends AbstractProvider {

    public HashProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                Hash.class,
                (container, args) -> new Hash(),
                "crypt"
            );
    }

    @Override
    public void boot() {
        this.app.make(Hash.class);
    }
}
