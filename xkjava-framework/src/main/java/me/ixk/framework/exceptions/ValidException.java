/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.exceptions;

import java.util.Map;
import javax.validation.ConstraintViolation;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;

/**
 * 验证异常
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:07
 */
public class ValidException extends HttpException {

    private static final long serialVersionUID = -7116939456564563129L;

    public ValidException(final ValidGroup validGroup) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup));
    }

    public ValidException(final ValidGroup validGroup, final Throwable cause) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup), cause);
    }

    public ValidException(
        final ValidGroup validGroup,
        final Map<String, String> headers
    ) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup), headers);
    }

    public ValidException(
        final ValidGroup validGroup,
        final Map<String, String> headers,
        final Throwable cause
    ) {
        super(
            HttpStatus.BAD_REQUEST,
            getErrorMessage(validGroup),
            headers,
            cause
        );
    }

    protected static String getErrorMessage(final ValidGroup validGroup) {
        final Map<String, ValidResult<Object>> errors = validGroup.getErrors();
        final StringBuilder builder = new StringBuilder(
            "Validation failed. Error count: " + errors.size() + "\n"
        );
        for (final Map.Entry<String, ValidResult<Object>> entry : errors.entrySet()) {
            final ValidResult<Object> result = entry.getValue();
            for (final Map.Entry<String, ConstraintViolation<Object>> fieldEntry : result
                .getErrors()
                .entrySet()) {
                final ConstraintViolation<Object> violation = fieldEntry.getValue();
                builder.append("Field error in object '");
                builder.append(entry.getKey());
                builder.append("' on field '");
                builder.append(fieldEntry.getKey());
                builder.append("': ");
                builder.append(violation.getMessage());
                builder.append("; rejected value [");
                builder.append(violation.getInvalidValue());
                builder.append("]\n");
            }
        }
        return builder.toString();
    }
}
