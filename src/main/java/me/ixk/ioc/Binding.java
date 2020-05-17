package me.ixk.ioc;

public class Binding {
    public boolean shared;
    public Concrete concrete;

    public Binding(Concrete concrete, boolean shared) {
        this.shared = shared;
        this.concrete = concrete;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public Concrete getConcrete() {
        return concrete;
    }

    public void setConcrete(Concrete concrete) {
        this.concrete = concrete;
    }
}
