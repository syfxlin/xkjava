/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.Exception;

//@ControllerAdvice
@Order(-1)
public class GlobalControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public String exception(final Exception e) {
        return "error";
    }
}
