/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.exceptions;

import java.util.Map;
import javax.validation.ConstraintViolation;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;

public class ValidException extends HttpException {

    public ValidException(ValidGroup validGroup) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup));
    }

    public ValidException(ValidGroup validGroup, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup), cause);
    }

    public ValidException(ValidGroup validGroup, Map<String, String> headers) {
        super(HttpStatus.BAD_REQUEST, getErrorMessage(validGroup), headers);
    }

    public ValidException(
        ValidGroup validGroup,
        Map<String, String> headers,
        Throwable cause
    ) {
        super(
            HttpStatus.BAD_REQUEST,
            getErrorMessage(validGroup),
            headers,
            cause
        );
    }

    protected static String getErrorMessage(ValidGroup validGroup) {
        Map<String, ValidResult<Object>> errors = validGroup.getErrors();
        StringBuilder builder = new StringBuilder(
            "Validation failed. Error count: " + errors.size() + "\n"
        );
        for (Map.Entry<String, ValidResult<Object>> entry : errors.entrySet()) {
            ValidResult<Object> result = entry.getValue();
            for (Map.Entry<String, ConstraintViolation<Object>> fieldEntry : result
                .getErrors()
                .entrySet()) {
                ConstraintViolation<Object> violation = fieldEntry.getValue();
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
