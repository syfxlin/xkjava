package me.ixk.framework.websocket;

import java.io.IOException;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:51
 */
public interface WebSocketFactory {
    /**
     * 启动
     *
     * @throws Exception 异常
     */
    void start() throws Exception;

    /**
     * 关闭
     *
     * @throws Exception 异常
     */
    void stop() throws Exception;

    /**
     * 处理请求
     *
     * @param handler  处理器
     * @param request  请求
     * @param response 响应
     * @return 是否请求完成
     * @throws IOException 异常
     */
    boolean accept(
        final Class<? extends WebSocketHandler> handler,
        final Request request,
        final Response response
    ) throws IOException;

    /**
     * 是否升级请求
     *
     * @param request  请求
     * @param response 响应
     * @return 是否升级
     */
    boolean isUpgradeRequest(final Request request, final Response response);
}
