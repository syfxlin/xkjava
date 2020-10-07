/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Config;
import me.ixk.framework.exceptions.AnnotationProcessorException;
import me.ixk.framework.exceptions.LoadConfigException;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.ClassUtils;

public class ConfigAnnotationProcessor extends AbstractAnnotationProcessor {

    public ConfigAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        throw new AnnotationProcessorException("Not support invoke process");
    }

    public Map<String, Map<String, Object>> processAnnotationConfig() {
        Map<String, Map<String, Object>> config = new ConcurrentHashMap<>();
        List<Class<?>> classes = this.getTypesAnnotated(Config.class);
        for (Class<?> _class : classes) {
            String name = AnnotationUtils
                .getAnnotation(_class, Config.class)
                .name();
            name = name.length() > 0 ? name : _class.getSimpleName();
            // 如果是 Config 类的子类，即编程化配置的方式，则通过 config 方法读取
            if (me.ixk.framework.config.Config.class.isAssignableFrom(_class)) {
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
        return config;
    }
}
