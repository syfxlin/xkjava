package me.ixk.framework.exceptions;

public class AnnotationProcessorException extends Exception {
    public AnnotationProcessorException() {
        super();
    }

    public AnnotationProcessorException(String message) {
        super(message);
    }

    public AnnotationProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationProcessorException(Throwable cause) {
        super(cause);
    }

    protected AnnotationProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
