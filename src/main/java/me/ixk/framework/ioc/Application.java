package me.ixk.framework.ioc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.annotations.processor.BootstrapAnnotationProcessor;
import me.ixk.framework.ioc.context.ApplicationContext;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.server.HttpServer;
import me.ixk.framework.utils.AnnotationUtils;

/**
 * Application 继承自 IoC Container，本身存储一般的是实例和实例的绑定
 * 如果要存储配置文件，和一些无关实例的字段，应该使用 ApplicationContext，可以直接使用 ApplicationContext 的静态方法
 * 或者使用 Application.get().getContext() 获取
 */
public class Application extends Container {
    protected Class<?>[] primarySource;

    protected String[] args;

    protected boolean booted = false;

    protected ProviderManager providerManager;

    protected AnnotationProcessorManager annotationProcessorManager;

    protected BootCallback bootingCallback = null;

    protected BootCallback bootedCallback = null;

    private Application() {
        ApplicationContext applicationContext = new ApplicationContext();
        this.registerContext(applicationContext);
        RequestContext requestContext = new RequestContext();
        this.registerContext(requestContext);

        this.instance(Application.class, this, "app");
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
        BootstrapAnnotationProcessor processor = new BootstrapAnnotationProcessor(
            this
        );
        processor.process();
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

    /* Quick get set context attribute */

    public List<String> getScanPackage() {
        return this.getOrDefaultAttribute("scanPackage", new ArrayList<>());
    }

    public void setScanPackage(List<String> scanPackage) {
        this.setAttribute("scanPackage", scanPackage);
    }

    public Map<String, Map<String, Object>> getConfig() {
        return this.getOrDefaultAttribute("config", new ConcurrentHashMap<>());
    }

    public void setConfig(Map<String, Map<String, Object>> config) {
        this.setAttribute("config", config);
    }

    public Properties getEnvironment() {
        return this.getOrDefaultAttribute("env", new Properties());
    }

    public void setEnvironment(Properties properties) {
        this.setAttribute("env", properties);
    }
}
