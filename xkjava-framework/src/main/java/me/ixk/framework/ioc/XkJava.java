/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.collection.ConcurrentHashSet;
import java.util.Set;
import me.ixk.framework.annotation.core.Profile;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.event.ApplicationBootedEvent;
import me.ixk.framework.event.ApplicationBootingEvent;
import me.ixk.framework.event.ApplicationDestroyedEvent;
import me.ixk.framework.event.ApplicationDestroyingEvent;
import me.ixk.framework.event.ApplicationListener;
import me.ixk.framework.event.EventPublisher;
import me.ixk.framework.ioc.context.PrototypeContext;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.context.SessionContext;
import me.ixk.framework.ioc.context.SingletonContext;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.BeanProcessorAnnotationProcessor;
import me.ixk.framework.kernel.BootstrapAnnotationProcessor;
import me.ixk.framework.kernel.ComponentScanAnnotationProcessor;
import me.ixk.framework.kernel.InjectorAnnotationProcessor;
import me.ixk.framework.kernel.LoadEnvironmentVariables;
import me.ixk.framework.kernel.LoadMetadata;
import me.ixk.framework.property.Environment;
import me.ixk.framework.property.Metadata;
import me.ixk.framework.server.JettyServer;
import me.ixk.framework.server.Server;
import me.ixk.framework.util.Ansi;
import me.ixk.framework.util.Ansi.Color;
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

    private static final String VERSION = "v1.0-SNAPSHOT";
    private static final String OTHER =
        Ansi.make(Color.BLUE).format("Author: Otstar Lin <syfxlin@gmail.com>") +
        Ansi.split() +
        Ansi
            .make(Color.CYAN)
            .format("Github: https://github.com/syfxlin/xkjava");

    private String bannerText =
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
    private Class<?>[] primarySource;

    /**
     * 外部传入的参数
     */
    private String[] args;

    /**
     * Bean 扫描器
     */
    private final BeanScanner beanScanner = new BeanScanner(this);

    /**
     * 元数据读取
     */
    private final LoadMetadata loadMetadata = new LoadMetadata(this);

    /**
     * 配置读取
     */
    private final LoadEnvironmentVariables loadEnvironmentVariables = new LoadEnvironmentVariables(
        this
    );

    /**
     * 开启的功能
     */
    private final Set<String> enableFunctions = new ConcurrentHashSet<>();

    /**
     * XkJava 是否已经启动
     */
    private boolean booted = false;

    /**
     * 组件注解扫描器
     */
    private final ComponentScanAnnotationProcessor componentScanAnnotationProcessor = new ComponentScanAnnotationProcessor(
        this
    );

    /**
     * 启动处理器
     */
    private final BootstrapAnnotationProcessor bootstrapAnnotationProcessor = new BootstrapAnnotationProcessor(
        this
    );

    /**
     * 注入器注解处理器
     */
    private final InjectorAnnotationProcessor injectorAnnotationProcessor = new InjectorAnnotationProcessor(
        this
    );

    /**
     * Bean 后置注解处理器
     */
    private final BeanProcessorAnnotationProcessor beanProcessorAnnotationProcessor = new BeanProcessorAnnotationProcessor(
        this
    );

    /**
     * 事件发布器
     */
    private final EventPublisher eventPublisher = new EventPublisher(this);

    /**
     * 注解处理器
     */
    private AnnotationProcessorManager annotationProcessorManager = new AnnotationProcessorManager(
        this
    );

    public static XkJava boot(
        final Class<?> primarySource,
        final String... args
    ) {
        return of().bootInner(primarySource, args);
    }

    public static XkJava boot(
        final Class<?>[] primarySource,
        final String... args
    ) {
        return of().bootInner(primarySource, args);
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
    public XkJava bootInner(
        final Class<?> primarySource,
        final String... args
    ) {
        return this.bootInner(new Class[] { primarySource }, args);
    }

    /**
     * 启动 XkJava 实例
     *
     * @param primarySource 传入类
     * @param args          传入参数
     */
    public XkJava bootInner(
        final Class<?>[] primarySource,
        final String... args
    ) {
        this.primarySource = primarySource;
        this.args = args;

        // 打印 Banner
        this.printBanner();

        // 注册实例
        this.registerInstance();

        // 注册销毁钩子
        this.registerShutdownHook();

        // 读取元配置
        this.loadMetadata.load();

        // 读取配置文件
        this.loadEnvironmentVariables.load();

        // 读取需要扫描的包
        this.componentScanAnnotationProcessor.process();

        // 导入 Injector 和 BeanProcessor
        this.injectorAnnotationProcessor.process();
        this.beanProcessorAnnotationProcessor.process();

        // 扫描事件
        this.eventPublisher.process();

        // 启动前回调
        if (log.isDebugEnabled()) {
            log.debug("Application call booting");
        }
        this.eventPublisher.publishEvent(new ApplicationBootingEvent(this));

        // 通过调用 Bootstrap 注解处理器处理 Bootstrap
        if (log.isDebugEnabled()) {
            log.debug("Application process bootstrap");
        }
        this.bootstrapAnnotationProcessor.process();

        this.booted = true;
        // 启动 Server 服务
        this.call(Server.class, "start", Void.class);

        log.info("Application booted");

        // 启动后回调
        if (log.isDebugEnabled()) {
            log.debug("Application call booted");
        }
        this.eventPublisher.publishEvent(new ApplicationBootedEvent(this));
        return this;
    }

    /**
     * 注册实例
     */
    private void registerInstance() {
        final SingletonContext singletonContext = new SingletonContext();
        this.registerContext(ScopeType.SINGLETON, singletonContext);
        final PrototypeContext prototypeContext = new PrototypeContext();
        this.registerContext(ScopeType.PROTOTYPE, prototypeContext);
        final RequestContext requestContext = new RequestContext();
        this.registerContext(ScopeType.REQUEST, requestContext);
        final SessionContext sessionContext = new SessionContext();
        this.registerContext(ScopeType.SESSION, sessionContext);

        this.instance("app", this);
        this.instance("loadMetadata", loadMetadata);
        this.instance("loadEnvironmentVariables", loadEnvironmentVariables);
        this.bind("aspectManager", AspectManager.class);
        this.instance("annotationProcessorManager", annotationProcessorManager);
        this.instance("beanScanner", this.beanScanner);
        this.instance("eventPublisher", this.eventPublisher);
    }

    /**
     * 注册销毁钩子
     */
    private void registerShutdownHook() {
        Runtime
            .getRuntime()
            .addShutdownHook(
                new Thread(
                    () -> {
                        log.info("Run shutdown hook");
                        this.destroy();
                    },
                    "shutdown-hook"
                )
            );
    }

    @Override
    public void destroy() {
        this.eventPublisher.publishEvent(new ApplicationDestroyingEvent(this));
        super.destroy();
        this.eventPublisher.publishEvent(new ApplicationDestroyedEvent(this));
    }

    private void printBanner() {
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

    public XkJava annotationProcessorManager(
        final AnnotationProcessorManager annotationProcessorManager
    ) {
        this.annotationProcessorManager = annotationProcessorManager;
        return this;
    }

    public Set<String> enableFunctions() {
        return enableFunctions;
    }

    /* Quick get set context attribute */

    public BeanScanner beanScanner() {
        return this.beanScanner;
    }

    public Environment env() {
        return this.make(Environment.class);
    }

    public Metadata metadata() {
        return this.make(Metadata.class);
    }

    public AnnotationProcessorManager annotationProcessorManager() {
        return annotationProcessorManager;
    }

    public EventPublisher event() {
        return eventPublisher;
    }

    /**
     * 静态内部类创建实例
     */
    private static class Inner {

        private static final XkJava INSTANCE = new XkJava();
    }

    public JettyServer server() {
        return this.make(JettyServer.class);
    }

    public boolean isBooted() {
        return this.booted;
    }

    public XkJava booting(final ApplicationListener listener) {
        this.eventPublisher.addListener(
                ApplicationBootingEvent.class,
                listener
            );
        return this;
    }

    public XkJava booted(final ApplicationListener listener) {
        this.eventPublisher.addListener(ApplicationBootedEvent.class, listener);
        return this;
    }

    public XkJava destroying(final ApplicationListener listener) {
        this.eventPublisher.addListener(
                ApplicationDestroyingEvent.class,
                listener
            );
        return this;
    }

    public XkJava destroyed(final ApplicationListener listener) {
        this.eventPublisher.addListener(
                ApplicationDestroyedEvent.class,
                listener
            );
        return this;
    }

    public String bannerText() {
        return this.bannerText;
    }

    public XkJava bannerText(final String bannerText) {
        this.bannerText = bannerText;
        return this;
    }

    public Class<?>[] primarySource() {
        return primarySource;
    }

    public String[] args() {
        return args;
    }

    public String profile() {
        return this.env().get("xkjava.config.active", Profile.PROD);
    }
}
