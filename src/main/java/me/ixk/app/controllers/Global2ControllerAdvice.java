package me.ixk.app.controllers;

import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.exceptions.Exception;

//@ControllerAdvice
//@Order(1)
public class Global2ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e) {
        return "error2";
    }
}
