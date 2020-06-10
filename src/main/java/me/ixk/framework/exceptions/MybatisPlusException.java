package me.ixk.framework.exceptions;

public class MybatisPlusException extends Exception {
    public MybatisPlusException() {
        super();
    }

    public MybatisPlusException(String message) {
        super(message);
    }

    public MybatisPlusException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisPlusException(Throwable cause) {
        super(cause);
    }

    protected MybatisPlusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
