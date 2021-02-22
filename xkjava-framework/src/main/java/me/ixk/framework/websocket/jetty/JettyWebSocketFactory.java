package me.ixk.framework.websocket.jetty;

import java.io.IOException;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.websocket.WebSocketFactory;
import me.ixk.framework.websocket.WebSocketHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 7:04
 */
@Component(name = "webSocketFactory")
public class JettyWebSocketFactory implements WebSocketFactory {

    private final WebSocketServletFactory factory = new WebSocketServerFactory();
    private final XkJava app;

    public JettyWebSocketFactory(final XkJava app) {
        this.app = app;
    }

    @Override
    public void start() throws Exception {
        factory.start();
    }

    @Override
    public void stop() throws Exception {
        factory.stop();
    }

    @Override
    public boolean accept(
        final Class<? extends WebSocketHandler> handler,
        final Request request,
        final Response response
    ) throws IOException {
        return factory.acceptWebSocket(
            new WebSocketCreator(handler),
            request,
            response
        );
    }

    @Override
    public boolean isUpgradeRequest(
        final Request request,
        final Response response
    ) {
        return factory.isUpgradeRequest(request, response);
    }

    private class WebSocketCreator
        implements org.eclipse.jetty.websocket.servlet.WebSocketCreator {

        private final Class<? extends WebSocketHandler> handler;

        public WebSocketCreator(
            final Class<? extends WebSocketHandler> handler
        ) {
            this.handler = handler;
        }

        @Override
        public Object createWebSocket(
            final ServletUpgradeRequest req,
            final ServletUpgradeResponse resp
        ) {
            return new JettyWebSocketHandler(app.make(this.handler));
        }
    }
}
