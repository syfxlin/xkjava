package me.ixk.framework.exceptions;

public class AspectProcessException extends Exception {
    public AspectProcessException() {
        super();
    }

    public AspectProcessException(String message) {
        super(message);
    }

    public AspectProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AspectProcessException(Throwable cause) {
        super(cause);
    }

    protected AspectProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
