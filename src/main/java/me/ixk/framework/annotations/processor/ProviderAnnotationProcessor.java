package me.ixk.framework.annotations.processor;

import java.util.List;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.providers.Provider;

public class ProviderAnnotationProcessor extends AbstractAnnotationProcessor {

    public ProviderAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        List<Class<?>> providers =
            this.getTypesAnnotatedWith(
                    me.ixk.framework.annotations.Provider.class
                );
        for (Class<?> providerType : providers) {
            if (!Provider.class.isAssignableFrom(providerType)) {
                throw new AnnotationProcessorException(
                    "Classes marked by the Provider annotation should implement the Provider interface"
                );
            }
        }
        ProviderManager providerManager = new ProviderManager(this.app);
        this.app.setProviderManager(providerManager);
        this.app.instance(
                ProviderManager.class,
                providerManager,
                "providerManager"
            );
        providerManager.registers(providers);
    }
}
