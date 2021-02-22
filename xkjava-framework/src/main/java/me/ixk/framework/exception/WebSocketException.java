package me.ixk.framework.exception;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 9:37
 */
public class WebSocketException extends Exception {

    public WebSocketException() {
        super();
    }

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebSocketException(Throwable cause) {
        super(cause);
    }

    protected WebSocketException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
