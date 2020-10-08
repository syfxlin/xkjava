/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.framework.exceptions.ProviderException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.providers.Provider;

public class ProviderManager {
    protected final XkJava app;

    protected final Map<String, Provider> providers;

    public ProviderManager(XkJava app) {
        this.app = app;
        this.providers = new ConcurrentHashMap<>();
    }

    public Provider getProvider(String name) {
        return this.providers.get(name);
    }

    public Provider getProvider(Provider provider) {
        return provider;
    }

    public boolean hasProvider(String name) {
        return this.providers.containsKey(name);
    }

    public void setProvider(String name, Provider provider) {
        this.providers.put(name, provider);
    }

    protected Provider getProviderInstance(String name) {
        try {
            Provider provider = (Provider) Class
                .forName(name)
                .getConstructor(XkJava.class)
                .newInstance(this.app);
            this.setProvider(name, provider);
            return provider;
        } catch (
            InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException
            | ClassNotFoundException e
        ) {
            throw new ProviderException("Instantiating provider failed", e);
        }
    }

    public Provider register(String provider) {
        return this.register(provider, false);
    }

    public Provider register(String provider, boolean force) {
        Provider result;
        if (!force && (result = this.getProvider(provider)) != null) {
            return result;
        }
        result = this.getProviderInstance(provider);
        result.register();
        return result;
    }

    public Provider register(Class<?> clazz) {
        return this.register(clazz.getName());
    }

    public Provider register(Class<?> clazz, boolean force) {
        return this.register(clazz.getName(), force);
    }

    public Set<Provider> registers(Set<Class<?>> providers) {
        return providers
            .stream()
            .map(this::register)
            .collect(Collectors.toSet());
    }

    public void boot() {
        this.providers.values()
            .forEach(
                provider -> {
                    if (!provider.isBooted()) {
                        provider.boot();
                        provider.setBooted(true);
                    }
                }
            );
    }
}
