/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.io.IoUtil;
import io.github.imsejin.expression.TypeConverter;
import io.github.imsejin.expression.core.convert.TypeDescriptor;
import io.github.imsejin.expression.spel.SpelEvaluationException;
import io.github.imsejin.expression.spel.SpelMessage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.ResourceUtils;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:15
 */
public class StandardTypeConverter implements TypeConverter {

    @Override
    public boolean canConvert(
        final TypeDescriptor sourceType,
        final TypeDescriptor targetType
    ) {
        return true;
    }

    @Override
    public Object convertValue(
        final Object value,
        final TypeDescriptor sourceType,
        final TypeDescriptor targetType
    ) {
        final Class<?> sourceClazz = sourceType.getType();
        final Class<?> targetClazz = targetType.getType();
        if (File.class == targetClazz || InputStream.class == targetClazz) {
            if (
                String.class == sourceClazz ||
                URL.class == sourceClazz ||
                URI.class == sourceClazz ||
                Path.class == sourceClazz
            ) {
                final Object result = this.convertFile(value, targetClazz);
                if (result != null) {
                    return result;
                }
            }
        }
        try {
            return Convert.convert(targetClazz, value);
        } catch (final ConvertException e) {
            throw new SpelEvaluationException(
                e,
                SpelMessage.TYPE_CONVERSION_ERROR,
                sourceType.toString(),
                targetType.toString()
            );
        }
    }

    private Object convertFile(final Object value, final Class<?> type) {
        File file = null;
        if (value instanceof URL) {
            file = ResourceUtils.getFile((URL) value);
        }
        if (value instanceof URI) {
            file = ResourceUtils.getFile((URI) value);
        }
        if (value instanceof String) {
            file = ResourceUtils.getFile((String) value);
        }
        if (value instanceof Path) {
            file = ResourceUtils.getFile(((Path) value).toUri());
        }
        if (file == null) {
            return null;
        }
        if (File.class == type) {
            return file;
        }
        if (InputStream.class == type) {
            return IoUtil.toStream(file);
        }
        return null;
    }
}
