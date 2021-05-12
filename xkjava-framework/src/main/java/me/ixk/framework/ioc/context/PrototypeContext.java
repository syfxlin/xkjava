package me.ixk.framework.ioc.context;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 9:46
 */
public class PrototypeContext implements Context {

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public void remove(String name) {
        throw new UnsupportedOperationException(
            "Unsupported remove instance from prototype"
        );
    }

    @Override
    public void set(String name, Object instance) {
        throw new UnsupportedOperationException(
            "Unsupported set instance to prototype"
        );
    }

    @Override
    public boolean has(String name) {
        return false;
    }
}
