package me.ixk.framework.http;

import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.MimeTypes;

public class ResponseProcessor {

    public static Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        if (result == null) {
            return response;
        } else if (result instanceof Response) {
            return response;
        } else if (result instanceof HttpServletResponse) {
            return response.setOriginResponse(
                (org.eclipse.jetty.server.Response) result
            );
        } else if (result instanceof Responsable) {
            return ((Responsable) result).toResponse(request, response, result);
        } else if (result instanceof String) {
            return response.content(result.toString());
        } else {
            return response
                .contentType(MimeTypes.Type.APPLICATION_JSON.asString())
                .content(JSON.stringify(result));
        }
    }

    public static Response dispatchResponse(Response response) {
        return response.pushCookieToHeader();
    }
}
