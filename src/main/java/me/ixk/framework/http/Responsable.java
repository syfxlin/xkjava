package me.ixk.framework.http;

public interface Responsable {
    Response toResponse(Request request, Response response, Object result);
}
