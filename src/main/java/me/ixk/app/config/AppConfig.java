package me.ixk.app.config;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.annotations.Log;
import me.ixk.framework.annotations.*;
import me.ixk.framework.annotations.processor.BeanAnnotationProcessor;
import me.ixk.framework.config.AbstractConfig;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.providers.AppProvider;
import me.ixk.framework.providers.AspectProvider;
import me.ixk.framework.providers.ThymeleafProvider;

public class AppConfig extends AbstractConfig {

    public AppConfig(Application app) {
        super(app);
    }

    @Override
    public String configName() {
        return "app";
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("name", this.env.get("app.name", "XK-Java"));
        map.put("version", this.env.get("app.version", "1.0"));
        map.put("locale", this.env.get("app.locale", "zh_CN"));
        map.put("env", this.env.get("app.env", "production"));
        map.put("url", this.env.get("app.url", "http://localhost"));
        map.put("asset_url", this.env.get("app.asset.url", ""));
        map.put("key", this.env.get("app.key"));
        map.put("cipher", this.env.get("app.cipher", "AES_256_CBC"));
        map.put("hash.algo", this.env.get("app.hash", "bcrypt"));
        map.put("jwt.algo", this.env.get("app.jwt", "HS256"));

        map.put("providers", this.providers());

        map.put("annotation_processors", this.annotationProcessors());

        map.put("bean_annotations", this.beanAnnotations());
        return map;
    }

    private List<Class<?>> providers() {
        return Arrays.asList(
            AppProvider.class,
            AspectProvider.class,
            ThymeleafProvider.class
        );
    }

    private List<Class<?>> annotationProcessors() {
        return Arrays.asList(BeanAnnotationProcessor.class);
    }

    private List<Class<? extends Annotation>> beanAnnotations() {
        return Arrays.asList(
            Bean.class,
            Component.class,
            Controller.class,
            Repository.class,
            Service.class,
            Aspect.class,
            Log.class
        );
    }
}
