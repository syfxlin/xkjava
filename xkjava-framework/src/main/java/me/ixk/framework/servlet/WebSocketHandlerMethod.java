package me.ixk.framework.servlet;

import me.ixk.framework.websocket.WebSocketHandler;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 5:13
 */
public class WebSocketHandlerMethod extends HandlerMethod {

    public WebSocketHandlerMethod(
        final Class<? extends WebSocketHandler> handler
    ) {
        super(() -> handler);
    }
}
