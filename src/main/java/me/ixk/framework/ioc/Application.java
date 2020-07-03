package me.ixk.framework.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.annotations.processor.BootstrapAnnotationProcessor;
import me.ixk.framework.ioc.context.ApplicationContext;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.Config;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.server.HttpServer;
import me.ixk.framework.utils.AnnotationUtils;

/**
 * Application 继承自 IoC 容器，并在其之上扩充一些应用部分的功能，同时也是框架的核心
 */
public class Application extends Container {
    /**
     * 存储 boot 方法传入的类
     */
    protected Class<?>[] primarySource;

    /**
     * 外部传入的参数
     */
    protected String[] args;

    /**
     * Application 是否已经启动
     */
    protected boolean booted = false;

    /**
     * 服务提供者管理器
     */
    protected ProviderManager providerManager;

    /**
     * 注解处理器
     */
    protected AnnotationProcessorManager annotationProcessorManager;

    /**
     * 启动前回调
     */
    protected Callback bootingCallback = null;

    /**
     * 启动后回调
     */
    protected Callback bootedCallback = null;

    /**
     * 销毁前回调
     */
    protected Callback destroyingCallback = null;

    /**
     * 销毁后回调
     */
    protected Callback destroyedCallback = null;

    private Application() {
        ApplicationContext applicationContext = new ApplicationContext();
        this.registerContext(applicationContext);
        RequestContext requestContext = new RequestContext();
        this.registerContext(requestContext);

        this.instance(Application.class, this, "app");

        // 注册销毁钩子
        this.registerShutdownHook();
    }

    /**
     * 静态内部类创建实例
     */
    private static class Inner {
        private static final Application INSTANCE = new Application();
    }

    /**
     * 创建或获取 Application
     * @return Application 实例
     */
    public static Application create() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 Application
     * @return Application 实例
     */
    public static Application getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 Application
     * @return Application 实例
     */
    public static Application get() {
        return Inner.INSTANCE;
    }

    /**
     * 创建并启动 Application 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public static void createAndBoot(Class<?> primarySource, String... args) {
        create().boot(new Class[] { primarySource }, args);
    }

    /**
     * 创建并启动 Application 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public static void createAndBoot(Class<?>[] primarySource, String... args) {
        create().boot(primarySource, args);
    }

    /**
     * 启动 Application 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public void boot(Class<?> primarySource, String... args) {
        boot(new Class[] { primarySource }, args);
    }

    /**
     * 启动 Application 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public void boot(Class<?>[] primarySource, String... args) {
        this.primarySource = primarySource;
        this.args = args;

        // 启动前读取一些信息
        this.load();

        // 启动前回调
        if (this.bootingCallback != null) {
            this.bootedCallback.invoke(this);
        }

        // Bootstrap
        this.bootstrap();

        // 启动后回调
        if (this.bootedCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.booted = true;

        // 启动 Jetty 服务
        this.startServer();
    }

    /**
     * 启动前读取一些信息
     */
    protected void load() {
        // 读取需要扫描的包
        this.loadPackageScanAnnotation();
    }

    /**
     * Bootstrap
     */
    protected void bootstrap() {
        // 通过调用 Bootstrap 注解处理器处理 Bootstrap
        BootstrapAnnotationProcessor processor = new BootstrapAnnotationProcessor(
            this
        );
        processor.process();
    }

    /**
     * 启动 Jetty Server
     */
    protected void startServer() {
        // 启动Jetty
        HttpServer server = HttpServer.create();
        this.instance(HttpServer.class, server, "server");
        server.start();
    }

    public boolean isBooted() {
        return this.booted;
    }

    public void booting(Callback callback) {
        this.bootingCallback = callback;
    }

    public void booted(Callback callback) {
        this.bootedCallback = callback;
    }

    public void destroying(Callback callback) {
        this.destroyingCallback = callback;
    }

    public void destroyed(Callback callback) {
        this.destroyedCallback = callback;
    }

    /**
     * 配置要扫描的包
     */
    protected void loadPackageScanAnnotation() {
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

    /**
     * 注册销毁钩子
     */
    protected void registerShutdownHook() {
        Runtime
            .getRuntime()
            .addShutdownHook(
                new Thread(
                    () -> {
                        System.out.println("destroy");
                        if (this.destroyingCallback != null) {
                            this.destroyingCallback.invoke(this);
                        }
                        this.destroy();
                        if (this.destroyedCallback != null) {
                            this.destroyedCallback.invoke(this);
                        }
                    }
                )
            );
    }

    /* Quick get set context attribute */

    public List<String> getScanPackage() {
        return this.getOrDefaultAttribute("scanPackage", new ArrayList<>());
    }

    public void setScanPackage(List<String> scanPackage) {
        this.setAttribute("scanPackage", scanPackage);
    }

    public Config getConfig() {
        return this.make(Config.class);
    }

    public Environment getEnvironment() {
        return this.make(Environment.class);
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
}
