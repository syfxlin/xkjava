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
import me.ixk.framework.server.HttpServer;
import me.ixk.framework.utils.AnnotationUtils;

public class Application extends Container {
    protected List<String> scanPackage = new ArrayList<>();

    protected Class<?>[] primarySource;

    protected String[] args;

    protected Map<String, Map<String, Object>> config = new ConcurrentHashMap<>();

    protected final List<Class<? extends Bootstrap>> bootstraps = Arrays.asList(
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

    public static Application getInstance() {
        return Inner.INSTANCE;
    }

    public static Application get() {
        return Inner.INSTANCE;
    }

    public static void createAndBoot(Class<?> primarySource, String... args) {
        create().boot(new Class[] { primarySource }, args);
    }

    public static void createAndBoot(Class<?>[] primarySource, String... args) {
        create().boot(primarySource, args);
    }

    public void boot(Class<?> primarySource, String... args) {
        boot(new Class[] { primarySource }, args);
    }

    public void boot(Class<?>[] primarySource, String... args) {
        this.primarySource = primarySource;
        this.args = args;

        this.load();

        if (this.bootingCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.bootstrap();

        if (this.bootedCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.booted = true;

        this.startServer();
    }

    protected void load() {
        this.loadPackageScanAnnotation();

        this.instance(Application.class, this, "app");

        ApplicationContext applicationContext = new ApplicationContext();
        ApplicationContext.setAttributes(applicationContext);
        this.instance(ApplicationContext.class, applicationContext, "context");
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

    protected void startServer() {
        // 启动Jetty
        HttpServer server = HttpServer.create();
        this.instance(HttpServer.class, server, "server");
        server.start();
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

    public List<String> getScanPackage() {
        return this.scanPackage;
    }

    public void setScanPackage(List<String> scanPackage) {
        this.scanPackage = scanPackage;
    }

    public Map<String, Map<String, Object>> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Map<String, Object>> config) {
        this.config = config;
    }

    public void loadPackageScanAnnotation() {
        scanPackage.add("me.ixk.framework");
        for (Class<?> source : this.primarySource) {
            scanPackage.add(source.getPackageName());
            ComponentScan componentScan = AnnotationUtils.getAnnotation(
                source,
                ComponentScan.class
            );
            if (componentScan != null) {
                scanPackage.addAll(Arrays.asList(componentScan.basePackages()));
            }
        }
    }
}
