package me.ixk.framework.annotations.processor;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Configuration;
import me.ixk.framework.config.Config;
import me.ixk.framework.exceptions.LoadConfigException;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

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

    private void processAnnotationConfig() {
        List<Class<?>> classes = this.getTypesAnnotated(Configuration.class);
        for (Class<?> _class : classes) {
            String name = AnnotationUtils
                .getAnnotation(_class, Configuration.class)
                .name();
            name = name.length() > 0 ? name : _class.getSimpleName();
            // 如果是 Config 类的子类，即编程化配置的方式，则通过 config 方法读取
            if (Config.class.isAssignableFrom(_class)) {
                try {
                    Object instance = ReflectUtil.newInstance(_class, app);
                    config.put(name, ReflectUtil.invoke(instance, "config"));
                } catch (Exception e) {
                    throw new LoadConfigException(
                        "Load [" + _class.getSimpleName() + "] config failed",
                        e
                    );
                }
            } else {
                try {
                    Object object = ReflectUtil.newInstance(_class);
                    Map<String, Object> item = new ConcurrentHashMap<>();
                    for (Method method : ClassUtils.getMethods(_class)) {
                        item.put(
                            method.getName(),
                            ReflectUtil.invoke(object, method)
                        );
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
