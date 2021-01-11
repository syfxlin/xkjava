package me.ixk.framework.servlet;

import javax.servlet.ServletException;
import me.ixk.framework.ioc.XkJava;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 7:36
 */
public class ConcurrentResultHandlerMethod extends InvocableHandlerMethod {

    public ConcurrentResultHandlerMethod(
        final XkJava app,
        final Object result
    ) {
        super(
            app,
            new HandlerMethod(
                () -> {
                    if (result instanceof Exception) {
                        throw (Exception) result;
                    } else if (result instanceof Throwable) {
                        throw new ServletException(
                            "Async processing failed",
                            (Throwable) result
                        );
                    }
                    return result;
                }
            )
        );
    }
}
