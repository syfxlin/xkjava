package me.ixk.framework.annotations.processor;

import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.config.Config;
import me.ixk.framework.exceptions.LoadConfigException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationAnnotationProcessor
    extends AbstractAnnotationProcessor {
    protected final Map<String, Map<String, Object>> config;

    public ConfigurationAnnotationProcessor(Application app) {
        super(app);
        config = app.getConfig();
    }

    @Override
    public void process() {
        this.processAnnotationConfig();
    }

    @SuppressWarnings("unchecked")
    private void processAnnotationConfig() {
        List<Class<?>> classes =
            this.getTypesAnnotated(Configuration.class);
        for (Class<?> _class : classes) {
            String name = AnnotationUtils
                .getAnnotation(_class, Configuration.class)
                .name();
            name = name.length() > 0 ? name : _class.getSimpleName();
            // 如果是 Config 类的子类，即编程化配置的方式，则通过 config 方法读取
            if (Config.class.isAssignableFrom(_class)) {
                try {
                    Object instance = _class
                        .getConstructor(Application.class)
                        .newInstance(app);
                    config.put(
                        name,
                        (Map<String, Object>) _class
                            .getMethod("config")
                            .invoke(instance)
                    );
                } catch (Exception e) {
                    throw new LoadConfigException(
                        "Load [" + _class.getSimpleName() + "] config failed",
                        e
                    );
                }
            } else {
                try {
                    Object object = _class.getConstructor().newInstance();
                    Map<String, Object> item = new ConcurrentHashMap<>();
                    for (Method method : ClassUtils.getMethods(_class)) {
                        item.put(method.getName(), method.invoke(object));
                    }

                    config.put(name, item);
                } catch (Exception e) {
                    throw new LoadConfigException(
                        "Load [" + _class.getSimpleName() + "] config failed",
                        e
                    );
                }
            }
        }
    }
}
