package me.ixk.middleware;

public class Middleware2 implements Middleware {

    @Override
    public Object handle(Object request, Runner next) {
        System.out.println("Middleware2");
        return next.handle(request + "middleware2");
    }
}
