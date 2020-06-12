package me.ixk.framework.ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.bootstrap.*;
import me.ixk.framework.exceptions.ApplicationException;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.utils.AnnotationUtils;

@ComponentScan(basePackages = { "me.ixk.app" })
public class Application extends Container {
    protected static List<String> scanPackage = new ArrayList<>();

    protected Map<String, Map<String, Object>> config = new ConcurrentHashMap<>();

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

    private Application() {
        this.loadPackageScanAnnotation();
    }

    private static class Inner {
        private static final Application INSTANCE = new Application();
    }

    public static Application create() {
        return Inner.INSTANCE;
    }

    public static Application create(List<String> _package) {
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

    public static List<String> getScanPackage() {
        return scanPackage;
    }

    public static void setScanPackage(List<String> scanPackage) {
        Application.scanPackage = scanPackage;
    }

    public Map<String, Map<String, Object>> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Map<String, Object>> config) {
        this.config = config;
    }

    public void loadPackageScanAnnotation() {
        scanPackage.add("me.ixk.framework");
        ComponentScan componentScan = AnnotationUtils.getAnnotation(
            Application.class,
            ComponentScan.class
        );
        scanPackage.addAll(Arrays.asList(componentScan.basePackages()));
    }
}
