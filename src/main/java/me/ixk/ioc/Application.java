package me.ixk.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.ixk.annotations.*;
import me.ixk.aop.AspectManager;
import me.ixk.bootstrap.*;
import me.ixk.kernel.ProviderManager;
import me.ixk.utils.ClassUtil;

public class Application extends Container {
    protected static List<String> scanPackages = Arrays.asList("me.ixk.beans");

    protected static List<Class<? extends Annotation>> beanAnnotations = Arrays.asList(
        Bean.class,
        Component.class,
        Controller.class,
        Repository.class,
        Service.class,
        Aspect.class,
        Log.class
    );

    protected List<Class<? extends Bootstrap>> bootstraps = Arrays.asList(
        LoadEnvironmentVariables.class,
        LoadConfiguration.class,
        RegisterFacades.class,
        RegisterProviders.class,
        BootProviders.class
    );

    protected boolean booted = false;

    protected ProviderManager providerManager;

    protected BootCallback bootingCallback = null;

    protected BootCallback bootedCallback = null;

    private Application() {}

    private static class Inner {
        private static final Application instance = new Application();
    }

    public static Application create() {
        return Inner.instance;
    }

    public static Application create(
        List<String> packages,
        List<Class<? extends Annotation>> annotations
    ) {
        scanPackages = packages;
        beanAnnotations = annotations;
        return Inner.instance;
    }

    public static Application getInstance() {
        return Inner.instance;
    }

    public static Application createAndBoot() {
        return create().boot();
    }

    public Application boot() {
        if (this.bootingCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.loadClass();
        AspectManager manager = new AspectManager(this);
        this.instance(AspectManager.class, manager);
        this.bootstrap();

        if (this.bootedCallback != null) {
            this.bootedCallback.invoke(this);
        }

        this.booted = true;
        return this;
    }

    public void loadClass() {
        Set<Class<?>> classes = scanPackages
            .stream()
            .map(ClassUtil::getPackageClass)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        for (Class<?> _class : classes) {
            boolean isShared = true;
            Scope scope = _class.getAnnotation(Scope.class);
            if (scope != null) {
                isShared = scope.value().equals("singleton");
            }
            Bean bean = _class.getAnnotation(Bean.class);
            if (bean != null) {
                this.bind(_class, _class, isShared);
                for (String name : bean.value()) {
                    this.bind(_class, _class, isShared, name);
                }
                continue;
            }
            for (Class<? extends Annotation> annotation : beanAnnotations) {
                if (_class.isAnnotationPresent(annotation)) {
                    this.bind(_class, _class, isShared);
                    break;
                }
            }
        }
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
                        e.printStackTrace();
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

    public boolean isBooted() {
        return this.booted;
    }

    public void booting(BootCallback callback) {
        this.bootingCallback = callback;
    }

    public void booted(BootCallback callback) {
        this.bootedCallback = callback;
    }
}
