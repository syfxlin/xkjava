/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.property;

import cn.hutool.core.io.IoUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import me.ixk.framework.exception.LoadEnvironmentFileException;
import me.ixk.framework.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties 文件配置数据源
 *
 * @author Otstar Lin
 * @date 2020/12/21 下午 9:20
 */
public class PropertiesPropertySource extends MapPropertySource<Object> {

    private static final Logger log = LoggerFactory.getLogger(
        PropertiesPropertySource.class
    );

    public PropertiesPropertySource(final String name, final String path) {
        this(name, Resource.create(path).getStream());
    }

    public PropertiesPropertySource(final String name, final File file) {
        this(name, file, null);
    }

    public PropertiesPropertySource(
        final String name,
        final InputStream stream
    ) {
        this(name, stream, null);
    }

    public PropertiesPropertySource(
        final String name,
        final File file,
        final String encoding
    ) {
        super(name, parseProperties(file, encoding));
    }

    public PropertiesPropertySource(
        final String name,
        final InputStream stream,
        final String encoding
    ) {
        super(name, parseProperties(stream, encoding));
    }

    private static Properties parseProperties(
        final File file,
        final String encoding
    ) {
        return parseProperties(IoUtil.toStream(file), encoding);
    }

    private static Properties parseProperties(
        final InputStream stream,
        final String encoding
    ) {
        final Properties properties = new Properties();
        try {
            properties.load(
                IoUtil.getReader(
                    stream,
                    encoding == null || encoding.isEmpty()
                        ? Charset.defaultCharset()
                        : Charset.forName(encoding)
                )
            );
        } catch (final IOException e) {
            log.error("Load environment [application.properties] failed");
            throw new LoadEnvironmentFileException(
                "Load environment [application.properties] failed",
                e
            );
        }
        return properties;
    }
}
