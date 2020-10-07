/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.util.Set;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.providers.Provider;

public class ProviderAnnotationProcessor extends AbstractAnnotationProcessor {

    public ProviderAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        Set<Class<?>> providers =
            this.getTypesAnnotated(me.ixk.framework.annotations.Provider.class);
        for (Class<?> providerType : providers) {
            if (!Provider.class.isAssignableFrom(providerType)) {
                throw new AnnotationProcessorException(
                    "Classes marked by the Provider annotation should implement the Provider interface"
                );
            }
        }
        this.app.providerManager().registers(providers);
    }
}
