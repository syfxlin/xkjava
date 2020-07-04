/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.exceptions;

public class RouteCollectorException extends Exception {
    public RouteCollectorException() {
        super();
    }

    public RouteCollectorException(String message) {
        super(message);
    }

    public RouteCollectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteCollectorException(Throwable cause) {
        super(cause);
    }

    protected RouteCollectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
