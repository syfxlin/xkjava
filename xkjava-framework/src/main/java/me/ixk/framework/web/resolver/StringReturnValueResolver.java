/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.WebResolver;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Model;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;

/**
 * 字符串响应解析器
 * <p>
 * 响应不按模板进行处理,即输出真实的字符串：":view:123" => "view:123" "::view:123" => ":view:123" ":view" => ":view"
 * <p>
 * 空响应： "empty:"
 * <p>
 * HTML 响应："html:html-string"
 * <p>
 * JSON 响应："json:json-string"
 * <p>
 * 跳转响应："redirect:url-string"
 * <p>
 * 文字响应："text:text-string"
 * <p>
 * 视图响应："view:view-string"
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 10:10
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class StringReturnValueResolver implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof String;
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        String result = (String) value;
        final int index = result.indexOf(":");
        final Model model = context.getApplication().make(Model.class);
        final HttpStatus status = model.getStatus();
        if (index != -1 && index != 0) {
            if (result.startsWith(Result.EMPTY_RETURN)) {
                return Result
                    .empty()
                    .status(status == null ? HttpStatus.OK : status);
            }
            if (result.startsWith(Result.HTML_RETURN)) {
                return Result
                    .html(result.substring(Result.HTML_RETURN.length()))
                    .status(status == null ? HttpStatus.OK : status);
            }
            if (result.startsWith(Result.JSON_RETURN)) {
                return Result
                    .stringJson(result.substring(Result.JSON_RETURN.length()))
                    .status(status == null ? HttpStatus.OK : status);
            }
            if (result.startsWith(Result.REDIRECT_RETURN)) {
                return Result
                    .redirect(result.substring(Result.REDIRECT_RETURN.length()))
                    .status(
                        status == null ? HttpStatus.MOVED_PERMANENTLY : status
                    );
            }
            if (result.startsWith(Result.TEXT_RETURN)) {
                return Result
                    .text(result.substring(Result.TEXT_RETURN.length()))
                    .status(status == null ? HttpStatus.OK : status);
            }
            if (result.startsWith(Result.VIEW_RETURN)) {
                return Result
                    .view(result.substring(Result.VIEW_RETURN.length()), model)
                    .status(status == null ? HttpStatus.OK : status);
            }
        }
        if (index == 0) {
            final int second = result.indexOf(":", 1);
            if (second != -1) {
                result = result.substring(1);
            }
        }
        return Result
            .text(result)
            .status(status == null ? HttpStatus.OK : status);
    }
}
