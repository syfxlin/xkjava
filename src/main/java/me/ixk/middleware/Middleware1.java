package me.ixk.middleware;

public class Middleware1 implements Middleware {

    @Override
    public Object handle(Object request, Runner next) {
        System.out.println("Middleware1");
        return next.handle(request + "middleware1");
    }
}
