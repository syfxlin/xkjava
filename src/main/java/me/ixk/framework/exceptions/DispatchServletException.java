package me.ixk.framework.exceptions;

public class DispatchServletException extends Exception {
    public DispatchServletException() {
        super();
    }

    public DispatchServletException(String message) {
        super(message);
    }

    public DispatchServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatchServletException(Throwable cause) {
        super(cause);
    }

    protected DispatchServletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
