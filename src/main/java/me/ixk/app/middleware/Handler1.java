package me.ixk.app.middleware;

import java.io.IOException;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Handler;

public class Handler1 implements Handler {

    @Override
    public Object handle(Request request, Response response) {
        try {
            return response.content("123");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}