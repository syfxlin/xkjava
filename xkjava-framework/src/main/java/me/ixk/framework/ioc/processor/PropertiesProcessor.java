/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.BeanProcessor;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.PropertySource;
import me.ixk.framework.bootstrap.LoadEnvironmentVariables;
import me.ixk.framework.exceptions.ContainerException;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.entity.InjectContext;
import me.ixk.framework.property.CompositePropertySource;
import me.ixk.framework.property.Environment;
import me.ixk.framework.property.MapPropertySource;
import me.ixk.framework.property.PropertiesPropertySource;
import me.ixk.framework.utils.DataUtils;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ResourceUtils;

/**
 * Properties 处理
 *
 * @author Otstar Lin
 * @date 2020/12/26 下午 9:29
 */
@BeanProcessor
@Order(Order.HIGHEST_PRECEDENCE)
public class PropertiesProcessor implements BeforeInjectProcessor {

    public static final String PROPERTIES_PREFIX = DataUtils.attributeName(
        PropertiesProcessor.class,
        "PREFIX"
    );
    public static final String PROPERTIES = DataUtils.attributeName(
        PropertiesProcessor.class,
        "PROPERTIES"
    );
    protected static final String PROPERTIES_SPLIT = ".";

    @Override
    public void process(Container container, InjectContext context) {
        if (this.isSkip(context.getType())) {
            return;
        }
        final ConfigurationProperties configurationProperties = context
            .getAnnotation()
            .getAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties == null
            ? ""
            : configurationProperties.prefix();
        if (!prefix.isEmpty() && !prefix.endsWith(PROPERTIES_SPLIT)) {
            prefix += PROPERTIES_SPLIT;
        }
        context.putData(PROPERTIES_PREFIX, prefix);
        context.putData(PROPERTIES, this.resolveProperties(container, context));
    }

    private boolean isSkip(Class<?> type) {
        // Disable some bootstrap
        return (
            type == LoadEnvironmentVariables.class || type == Environment.class
        );
    }

    private CompositePropertySource resolveProperties(
        Container container,
        InjectContext context
    ) {
        final MergedAnnotation annotation = context.getAnnotation();
        final List<PropertySource> propertySources = annotation.getAnnotations(
            PropertySource.class
        );
        final CompositePropertySource compositePropertySource = new CompositePropertySource(
            context.getType().getName()
        );
        compositePropertySource.setPropertySource(
            container.make(Environment.class)
        );
        if (!propertySources.isEmpty()) {
            for (final PropertySource propertySource : propertySources) {
                this.loadPropertySource(
                        compositePropertySource,
                        propertySource
                    );
            }
        }
        return compositePropertySource;
    }

    private void loadPropertySource(
        final CompositePropertySource compositePropertySource,
        final PropertySource propertySource
    ) {
        final String name = propertySource.location();
        if (!name.isEmpty()) {
            try {
                final File file = ResourceUtils.getFile(name);
                final String encoding = propertySource.encoding();
                compositePropertySource.setPropertySource(
                    new PropertiesPropertySource(
                        compositePropertySource.getName() + ":" + name,
                        file,
                        encoding
                    )
                );
            } catch (final FileNotFoundException e) {
                if (!propertySource.ignoreResourceNotFound()) {
                    throw new ContainerException(e);
                }
            }
        }
        if (propertySource.value().length > 0) {
            Map<String, Object> map = new HashMap<>(
                propertySource.value().length
            );
            for (final String value : propertySource.value()) {
                final String[] kv = value.split("=");
                map.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
            compositePropertySource.setPropertySource(
                new MapPropertySource<>(
                    compositePropertySource.getName() + ":inline",
                    map
                )
            );
        }
    }
}
