/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.annotations.processor.AnnotationProcessor;
import me.ixk.framework.annotations.processor.BootstrapAnnotationProcessor;
import me.ixk.framework.ioc.context.ApplicationContext;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.Config;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.kernel.ProviderManager;
import me.ixk.framework.server.JettyServer;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Ansi;

/**
 * XkJava 继承自 IoC 容器，并在其之上扩充一些应用部分的功能，同时也是框架的核心
 */
public class XkJava extends Container {
    protected String bannerText =
        " __   __      __  __               _____                                 \n" +
        "/\\ \\ /\\ \\    /\\ \\/\\ \\             /\\___ \\                                \n" +
        "\\ `\\`\\/'/'   \\ \\ \\/'/'            \\/__/\\ \\     __      __  __     __     \n" +
        " `\\/ > <      \\ \\ , <     _______    _\\ \\ \\  /'__`\\   /\\ \\/\\ \\  /'__`\\   \n" +
        "    \\/'/\\`\\    \\ \\ \\\\`\\  /\\______\\  /\\ \\_\\ \\/\\ \\L\\.\\_ \\ \\ \\_/ |/\\ \\L\\.\\_ \n" +
        "    /\\_\\\\ \\_\\   \\ \\_\\ \\_\\\\/______/  \\ \\____/\\ \\__/.\\_\\ \\ \\___/ \\ \\__/.\\_\\\n" +
        "    \\/_/ \\/_/    \\/_/\\/_/            \\/___/  \\/__/\\/_/  \\/__/   \\/__/\\/_/";
    /**
     * 存储 boot 方法传入的类
     */
    protected Class<?>[] primarySource;

    /**
     * 外部传入的参数
     */
    protected String[] args;

    /**
     * XkJava 是否已经启动
     */
    protected boolean booted = false;

    protected BootstrapAnnotationProcessor bootstrapAnnotationProcessor = new BootstrapAnnotationProcessor(
        this
    );

    /**
     * 服务提供者管理器
     */
    protected ProviderManager providerManager = new ProviderManager(this);

    /**
     * 注解处理器
     */
    protected AnnotationProcessorManager annotationProcessorManager = new AnnotationProcessorManager(
        this
    );

    /**
     * Jetty Server
     */
    protected JettyServer server = new JettyServer(this);

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

    private XkJava() {
        // 注册实例
        this.registerInstance();

        // 注册销毁钩子
        this.registerShutdownHook();
    }

    /**
     * 静态内部类创建实例
     */
    private static class Inner {
        private static final XkJava INSTANCE = new XkJava();
    }

    /**
     * 创建或获取 XkJava
     * @return XkJava 实例
     */
    public static XkJava create() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 XkJava
     * @return XkJava 实例
     */
    public static XkJava getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 XkJava
     * @return XkJava 实例
     */
    public static XkJava of() {
        return Inner.INSTANCE;
    }

    /**
     * 启动 XkJava 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public void boot(Class<?> primarySource, String... args) {
        this.boot(new Class[] { primarySource }, args);
    }

    /**
     * 启动 XkJava 实例
     * @param primarySource 传入类
     * @param args 传入参数
     */
    public void boot(Class<?>[] primarySource, String... args) {
        this.primarySource = primarySource;
        this.args = args;

        this.printBanner();

        // 读取需要扫描的包
        this.loadPackageScanAnnotation();

        // 启动前回调
        if (this.bootingCallback != null) {
            this.bootedCallback.invoke(this);
        }

        // 通过调用 Bootstrap 注解处理器处理 Bootstrap
        this.bootstrapAnnotationProcessor.process();

        // 启动后回调
        if (this.bootedCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.booted = true;
        // 启动 Jetty 服务
        this.server.start();
    }

    /**
     * 配置要扫描的包
     */
    protected void loadPackageScanAnnotation() {
        List<String> scanPackage = this.scanPackage();
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
     * 注册实例
     */
    protected void registerInstance() {
        ApplicationContext applicationContext = new ApplicationContext();
        this.registerContext(applicationContext);
        RequestContext requestContext = new RequestContext();
        this.registerContext(requestContext);

        this.instance(XkJava.class, this, "app");

        this.instance(JettyServer.class, this.server, "server");

        this.instance(
                ProviderManager.class,
                this.providerManager,
                "providerManager"
            );

        this.instance(
                AnnotationProcessor.class,
                annotationProcessorManager,
                "annotationProcessorManager"
            );
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

    protected void printBanner() {
        if (this.bannerText != null) {
            System.out.println(this.bannerText);
        }
        String text =
            Ansi.make(Ansi.Color.CYAN).format(" :: XK-Java :: ") +
            Ansi.make(Ansi.Color.MAGENTA).format("       (v1.0-SNAPSHOT)\n");
        System.out.println(text);
    }

    /* Quick get set context attribute */

    public List<String> scanPackage() {
        return this.getOrDefaultAttribute("scanPackage", new ArrayList<>());
    }

    public XkJava scanPackage(List<String> scanPackage) {
        this.setAttribute("scanPackage", scanPackage);
        return this;
    }

    public Config config() {
        return this.make(Config.class);
    }

    public Environment env() {
        return this.make(Environment.class);
    }

    public ProviderManager providerManager() {
        return providerManager;
    }

    public XkJava providerManager(ProviderManager providerManager) {
        this.providerManager = providerManager;
        return this;
    }

    public AnnotationProcessorManager annotationProcessorManager() {
        return annotationProcessorManager;
    }

    public XkJava annotationProcessorManager(
        AnnotationProcessorManager annotationProcessorManager
    ) {
        this.annotationProcessorManager = annotationProcessorManager;
        return this;
    }

    public JettyServer server() {
        return this.make(JettyServer.class);
    }

    public boolean isBooted() {
        return this.booted;
    }

    public XkJava booting(Callback callback) {
        this.bootingCallback = callback;
        return this;
    }

    public XkJava booted(Callback callback) {
        this.bootedCallback = callback;
        return this;
    }

    public XkJava destroying(Callback callback) {
        this.destroyingCallback = callback;
        return this;
    }

    public XkJava destroyed(Callback callback) {
        this.destroyedCallback = callback;
        return this;
    }

    public String bannerText() {
        return this.bannerText;
    }

    public XkJava bannerText(String bannerText) {
        this.bannerText = bannerText;
        return this;
    }
}