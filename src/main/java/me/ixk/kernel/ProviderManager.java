package me.ixk.kernel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.ioc.Application;
import me.ixk.providers.Provider;

public class ProviderManager {
    protected Application app;

    protected Map<String, Provider> providers;

    public ProviderManager(Application app) {
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
                .getConstructor(Application.class)
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
            e.printStackTrace();
        }
        return null;
    }

    public Provider register(String provider) {
        return this.register(provider, false);
    }

    public Provider register(String provider, boolean force) {
        Provider result = null;
        if (!force && (result = this.getProvider(provider)) != null) {
            return result;
        }
        result = this.getProviderInstance(provider);
        result.register();
        return result;
    }

    public List<Provider> registers(List<Class<?>> providers) {
        return providers
            .stream()
            .map(provider -> this.register(provider.getName()))
            .collect(Collectors.toList());
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
