package me.ixk.middleware;

public class Handler1 implements HandlerInterface {

    @Override
    public Object handle(Object request) {
        System.out.println("Handler1");
        return request;
    }
}
