package me.ixk.framework.http;

import org.eclipse.jetty.http.MimeTypes;

public interface Renderable extends Responsable {
    String render();

    default String contentType() {
        return MimeTypes.Type.TEXT_PLAIN.asString();
    }

    @Override
    default Response toResponse(
        Request request,
        Response response,
        Object result
    ) {
        return response.contentType(this.contentType()).content(this.render());
    }
}
