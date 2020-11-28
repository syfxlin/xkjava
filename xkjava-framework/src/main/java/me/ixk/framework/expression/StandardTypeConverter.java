/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import cn.hutool.core.convert.ConvertException;
import io.github.imsejin.expression.TypeConverter;
import io.github.imsejin.expression.core.convert.TypeDescriptor;
import io.github.imsejin.expression.spel.SpelEvaluationException;
import io.github.imsejin.expression.spel.SpelMessage;
import me.ixk.framework.utils.Convert;

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
        try {
            return Convert.convert(targetType.getType(), value);
        } catch (final ConvertException e) {
            throw new SpelEvaluationException(
                e,
                SpelMessage.TYPE_CONVERSION_ERROR,
                sourceType != null
                    ? sourceType.toString()
                    : (value != null ? value.getClass().getName() : "null"),
                targetType.toString()
            );
        }
    }
}
