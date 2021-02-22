package me.ixk.framework.web.resolver;

import java.io.File;
import java.nio.file.Path;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.WebAsync;
import me.ixk.framework.annotation.web.WebResolver;
import me.ixk.framework.http.result.FileResult;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebAsyncTask;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 9:22
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class FileResponseReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return (
            value instanceof FileResult ||
            value instanceof File ||
            value instanceof Path
        );
    }

    @Override
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        final FileResult result;
        if (value instanceof FileResult) {
            result = (FileResult) value;
        } else if (value instanceof Path) {
            result = new FileResult(((Path) value).toFile());
        } else if (value instanceof File) {
            result = new FileResult((File) value);
        } else {
            return value;
        }
        if (!result.async()) {
            return result;
        }
        final WebAsyncTask<Boolean> asyncTask = new WebAsyncTask<>(
            () ->
                result.toResponse(
                    context.getRequest(),
                    context.getResponse(),
                    value
                )
        );
        final WebAsync webAsync = returnValue
            .getMethodAnnotation()
            .getAnnotation(WebAsync.class);
        if (webAsync != null && !webAsync.value().isEmpty()) {
            asyncTask.setExecutorName(webAsync.value());
        }
        context.getAsyncManager().startAsync(asyncTask);
        return null;
    }
}
