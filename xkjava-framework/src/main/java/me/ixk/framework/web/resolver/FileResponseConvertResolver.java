package me.ixk.framework.web.resolver;

import java.io.File;
import java.nio.file.Path;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.http.result.FileResult;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.WebContext;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 9:22
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class FileResponseConvertResolver implements ResponseConvertResolver {

    @Override
    public boolean supportsConvert(
        Object value,
        WebContext context,
        RouteInfo info
    ) {
        return (
            value instanceof FileResult ||
            value instanceof File ||
            value instanceof Path
        );
    }

    @Override
    public boolean resolveConvert(
        Object value,
        WebContext context,
        RouteInfo info
    ) {
        final FileResult result;
        if (value instanceof FileResult) {
            result = (FileResult) value;
        } else if (value instanceof Path) {
            result = new FileResult(((Path) value).toFile());
        } else if (value instanceof File) {
            result = new FileResult((File) value);
        } else {
            return false;
        }
        context
            .getAsyncManager()
            .startAsync(
                () ->
                    result.toResponse(
                        context.getRequest(),
                        context.getResponse(),
                        value
                    )
            );
        return true;
    }
}
