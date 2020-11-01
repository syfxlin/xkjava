/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

/**
 * 可渲染
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:42
 */
@FunctionalInterface
public interface Renderable extends Responsible {
    /**
     * 渲染
     *
     * @return 渲染结果
     */
    String render();

    /**
     * 渲染后的文本类型
     *
     * @return 类型
     */
    default String contentType() {
        return MimeType.TEXT_PLAIN.asString();
    }

    /**
     * 转化为响应
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param result   返回值
     *
     * @return 响应对象
     */
    @Override
    default Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        return response.contentType(this.contentType()).content(this.render());
    }
}
