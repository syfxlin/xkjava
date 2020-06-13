package me.ixk.app.controllers;

import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.Exception;

@ControllerAdvice
@Order(-1)
public class GlobalControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e) {
        return "error";
    }
}
