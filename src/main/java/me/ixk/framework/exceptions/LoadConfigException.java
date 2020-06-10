package me.ixk.framework.exceptions;

public class LoadConfigException extends Exception {
    public LoadConfigException() {
        super();
    }

    public LoadConfigException(String message) {
        super(message);
    }

    public LoadConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadConfigException(Throwable cause) {
        super(cause);
    }

    protected LoadConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
