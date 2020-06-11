package me.ixk.framework.ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.bootstrap.*;
import me.ixk.framework.exceptions.ApplicationException;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.ProviderManager;

public class Application extends Container {
    protected static String[] scanPackage = new String[] { "me.ixk.app" };

    protected List<Class<? extends Bootstrap>> bootstraps = Arrays.asList(
        LoadEnvironmentVariables.class,
        LoadConfiguration.class,
        RegisterFacades.class,
        ProcessAnnotation.class,
        RegisterProviders.class,
        BootProviders.class
    );

    protected boolean booted = false;

    protected ProviderManager providerManager;

    protected AnnotationProcessorManager annotationProcessorManager;

    protected BootCallback bootingCallback = null;

    protected BootCallback bootedCallback = null;

    private Application() {}

    private static class Inner {
        private static final Application INSTANCE = new Application();
    }

    public static Application create() {
        return Inner.INSTANCE;
    }

    public static Application create(String[] _package) {
        scanPackage = _package;
        return Inner.INSTANCE;
    }

    public static Application getInstance() {
        return Inner.INSTANCE;
    }

    public static Application get() {
        return Inner.INSTANCE;
    }

    public static Application createAndBoot() {
        return create().boot();
    }

    public Application boot() {
        if (this.bootingCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.bootstrap();

        if (this.bootedCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.booted = true;
        return this;
    }

    protected void bootstrap() {
        this.bootstraps.forEach(
                bootstrap -> {
                    try {
                        bootstrap
                            .getConstructor(Application.class)
                            .newInstance(this)
                            .boot();
                    } catch (
                        InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e
                    ) {
                        throw new ApplicationException(
                            "Target [" +
                            bootstrap.getSimpleName() +
                            "] bootstrap error",
                            e
                        );
                    }
                }
            );
    }

    public ProviderManager getProviderManager() {
        return providerManager;
    }

    public void setProviderManager(ProviderManager providerManager) {
        this.providerManager = providerManager;
    }

    public AnnotationProcessorManager getAnnotationProcessorManager() {
        return annotationProcessorManager;
    }

    public void setAnnotationProcessorManager(
        AnnotationProcessorManager annotationProcessorManager
    ) {
        this.annotationProcessorManager = annotationProcessorManager;
    }

    public boolean isBooted() {
        return this.booted;
    }

    public void booting(BootCallback callback) {
        this.bootingCallback = callback;
    }

    public void booted(BootCallback callback) {
        this.bootedCallback = callback;
    }

    public static String[] getScanPackage() {
        return scanPackage;
    }

    public static void setScanPackage(String[] scanPackage) {
        Application.scanPackage = scanPackage;
    }
}
