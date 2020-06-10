package me.ixk.framework.exceptions;

public class LoadEnvironmentFileException extends Exception {
    public LoadEnvironmentFileException() {
        super();
    }

    public LoadEnvironmentFileException(String message) {
        super(message);
    }

    public LoadEnvironmentFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadEnvironmentFileException(Throwable cause) {
        super(cause);
    }

    protected LoadEnvironmentFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
