package me.ixk.app.middleware;

import me.ixk.framework.middleware.Handler;

public class Handler1 implements Handler {

    @Override
    public Object handle(Object request) {
        System.out.println("Handler1");
        return request;
    }
}
