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

/**
 * Application 继承自 IoC Container，本身存储一般的是实例和实例的绑定
 * 如果要存储配置文件，和一些无关实例的字段，应该使用 ApplicationContext，可以直接使用 ApplicationContext 的静态方法
 * 或者使用 Application.get().getContext() 获取
 */
public class Application extends Container implements Attributes {
    protected Class<?>[] primarySource;

    protected String[] args;

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

    protected ApplicationContext context;

    private Application() {
        this.context = ApplicationContext.create();

        this.instance(Application.class, this, "app");
        this.instance(ApplicationContext.class, this.context, "context");
    }

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

    public void loadPackageScanAnnotation() {
        List<String> scanPackage = this.getScanPackage();
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

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object getAttribute(String name) {
        return this.context.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object attribute) {
        this.context.setAttribute(name, attribute);
    }

    @Override
    public void removeAttribute(String name) {
        this.context.removeAttribute(name);
    }

    @Override
    public String[] getAttributeNames() {
        return this.context.getAttributeNames();
    }

    /* Quick get set context attribute */

    public List<String> getScanPackage() {
        return this.context.getOrDefaultAttribute(
                "scanPackage",
                new ArrayList<>()
            );
    }

    public void setScanPackage(List<String> scanPackage) {
        this.context.setAttribute("scanPackage", scanPackage);
    }

    public Map<String, Map<String, Object>> getConfig() {
        return this.context.getOrDefaultAttribute(
                "config",
                new ConcurrentHashMap<>()
            );
    }

    public void setConfig(Map<String, Map<String, Object>> config) {
        this.context.setAttribute("config", config);
    }
}
