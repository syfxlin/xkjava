/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotation.WebResolver;
import me.ixk.framework.exception.ResponseException;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.Responsible;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;

/**
 * 可响应返回值解析器
 *
 * @author Otstar Lin
 * @date 2020/11/1 下午 9:22
 */
@WebResolver
public class ResponsibleResponseConvertResolver
    implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        Object value,
        WebContext context,
        RouteInfo info
    ) {
        return (
            value == null ||
            value instanceof Responsible ||
            value instanceof HttpServletResponse
        );
    }

    @Override
    public boolean resolveConvert(
        Object value,
        WebContext context,
        RouteInfo info
    ) {
        if (value == null) {
            return true;
        }
        if (value instanceof Responsible) {
            try {
                return ((Responsible) value).toResponse(
                        context.getRequest(),
                        context.getResponse(),
                        value
                    );
            } catch (IOException e) {
                throw new ResponseException(e);
            }
        }
        if (value instanceof Response) {
            return true;
        }
        if (value instanceof HttpServletResponse) {
            final Response response = context.getResponse();
            response.setResponse((HttpServletResponse) value);
            return true;
        }
        throw new ResponseException(
            "The return value cannot be converted into a response. [" +
            value.getClass() +
            "]"
        );
    }
}
