/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.ioc.context.ApplicationContext;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.processor.AnnotationProcessor;
import me.ixk.framework.processor.BootstrapAnnotationProcessor;
import me.ixk.framework.server.JettyServer;
import me.ixk.framework.server.Server;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.Ansi;
import me.ixk.framework.utils.Ansi.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App 核心
 * <p>
 * XkJava 继承自 IoC 容器，并在其之上扩充一些应用部分的功能，同时也是框架的核心
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 12:45
 */
public class XkJava extends Container {
    private static final Logger log = LoggerFactory.getLogger(XkJava.class);

    protected static final String VERSION = "v1.0-SNAPSHOT";
    protected static final String OTHER =
        Ansi.make(Color.BLUE).format("Author: Otstar Lin <syfxlin@gmail.com>") +
        Ansi.split() +
        Ansi
            .make(Color.CYAN)
            .format("Github: https://github.com/syfxlin/xkjava");

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
     * 扫描组件的包
     */
    protected Set<String> scansPackages = new HashSet<>();

    /**
     * XkJava 是否已经启动
     */
    protected boolean booted = false;

    /**
     * 启动处理器
     */
    protected BootstrapAnnotationProcessor bootstrapAnnotationProcessor = new BootstrapAnnotationProcessor(
        this
    );

    /**
     * 注解处理器
     */
    protected AnnotationProcessorManager annotationProcessorManager = new AnnotationProcessorManager(
        this
    );

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
        // 打印 Banner
        this.printBanner();

        // 注册实例
        this.registerInstance();

        // 注册销毁钩子
        this.registerShutdownHook();

        log.info("Application created");
    }

    /**
     * 静态内部类创建实例
     */
    private static class Inner {
        private static final XkJava INSTANCE = new XkJava();
    }

    /**
     * 创建或获取 XkJava
     *
     * @return XkJava 实例
     */
    public static XkJava create() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 XkJava
     *
     * @return XkJava 实例
     */
    public static XkJava getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 创建或获取 XkJava
     *
     * @return XkJava 实例
     */
    public static XkJava of() {
        return Inner.INSTANCE;
    }

    /**
     * 启动 XkJava 实例
     *
     * @param primarySource 传入类
     * @param args          传入参数
     */
    public void boot(final Class<?> primarySource, final String... args) {
        this.boot(new Class[] { primarySource }, args);
    }

    /**
     * 启动 XkJava 实例
     *
     * @param primarySource 传入类
     * @param args          传入参数
     */
    public void boot(final Class<?>[] primarySource, final String... args) {
        this.primarySource = primarySource;
        this.args = args;

        // 读取需要扫描的包
        this.loadPackageScanAnnotation();

        // 启动前回调
        if (this.bootingCallback != null) {
            log.debug("Application call booting");
            this.bootedCallback.invoke(this);
        }

        // 通过调用 Bootstrap 注解处理器处理 Bootstrap
        log.debug("Application process bootstrap");
        this.bootstrapAnnotationProcessor.process();

        // 启动后回调
        if (this.bootedCallback != null) {
            log.debug("Application call booted");
            this.bootedCallback.invoke(this);
        }

        this.booted = true;
        // 启动 Server 服务
        this.call(Server.class, "start", Void.class);

        log.info("Application booted");
    }

    /**
     * 配置要扫描的包
     */
    protected void loadPackageScanAnnotation() {
        final Set<String> scanPackage = this.scanPackage();
        scanPackage.add("me.ixk.framework");
        for (final Class<?> source : this.primarySource) {
            scanPackage.add(source.getPackageName());
            final List<ComponentScan> componentScan = AnnotationUtils
                .getAnnotation(source)
                .getAnnotations(ComponentScan.class);
            if (componentScan != null) {
                for (ComponentScan scan : componentScan) {
                    this.loadPackageScanAnnotationItem(scanPackage, scan);
                }
            }
        }
    }

    protected void loadPackageScanAnnotationItem(
        Set<String> scanPackage,
        ComponentScan componentScan
    ) {
        scanPackage.addAll(Arrays.asList(componentScan.basePackages()));
        log.debug(
            "Application add base packages: {}",
            Arrays.toString(componentScan.basePackages())
        );
    }

    /**
     * 注册实例
     */
    protected void registerInstance() {
        final ApplicationContext applicationContext = new ApplicationContext();
        this.registerContext(applicationContext);
        final RequestContext requestContext = new RequestContext();
        this.registerContext(requestContext);

        this.instance(XkJava.class, this, "app");

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
                        log.info("Run shutdown hook");
                        if (this.destroyingCallback != null) {
                            this.destroyingCallback.invoke(this);
                        }
                        this.destroy();
                        if (this.destroyedCallback != null) {
                            this.destroyedCallback.invoke(this);
                        }
                    },
                    "shutdown-hook"
                )
            );
    }

    protected void printBanner() {
        if (this.bannerText != null) {
            System.out.println(this.bannerText + "\n");
        }
        final String text =
            Ansi.make(Color.CYAN).format(" :: XK-Java :: ") +
            Ansi.split() +
            Ansi.make(Color.MAGENTA).format("(" + VERSION + ")") +
            Ansi.split() +
            OTHER +
            "\n";
        System.out.println(text);
    }

    /* Quick get set context attribute */

    public Set<String> scanPackage() {
        return this.scansPackages;
    }

    public XkJava scanPackage(final Set<String> scanPackage) {
        this.scansPackages.addAll(scanPackage);
        return this;
    }

    public Environment env() {
        return this.make(Environment.class);
    }

    public AnnotationProcessorManager annotationProcessorManager() {
        return annotationProcessorManager;
    }

    public XkJava annotationProcessorManager(
        final AnnotationProcessorManager annotationProcessorManager
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

    public XkJava booting(final Callback callback) {
        this.bootingCallback = callback;
        return this;
    }

    public XkJava booted(final Callback callback) {
        this.bootedCallback = callback;
        return this;
    }

    public XkJava destroying(final Callback callback) {
        this.destroyingCallback = callback;
        return this;
    }

    public XkJava destroyed(final Callback callback) {
        this.destroyedCallback = callback;
        return this;
    }

    public String bannerText() {
        return this.bannerText;
    }

    public XkJava bannerText(final String bannerText) {
        this.bannerText = bannerText;
        return this;
    }
}
