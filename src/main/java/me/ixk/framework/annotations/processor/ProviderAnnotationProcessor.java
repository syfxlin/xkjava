/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.providers.Provider;

import java.util.List;

public class ProviderAnnotationProcessor extends AbstractAnnotationProcessor {

    public ProviderAnnotationProcessor(Application app) {
        super(app);
    }

    @Override
    public void process() {
        List<Class<?>> providers =
            this.getTypesAnnotated(
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
